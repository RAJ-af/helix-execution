import asyncio
import json
from typing import Any, List, Optional
from fastapi import APIRouter, Depends, HTTPException, Request, status
from sse_starlette.sse import EventSourceResponse
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, desc, func, update
from app.api import deps
from app.db.session import SessionLocal
from app.models.chat import Conversation, Message
from app.models.user import User
from app.schemas.chat import (
    Conversation as ConversationSchema,
    ConversationCreate,
    ConversationUpdate,
    ConversationWithMessages,
    Message as MessageSchema,
    MessageCreate
)
from app.services.search_service import SearchService, CitationExtractor, FollowUpGenerator
from app.services.agent_service import TaskPlanner, ClarificationManager
from app.services.ai.router import ModelRouter
from app.services.ai.prompts import PromptManager
from app.core.tools.executor import SubprocessExecutor
from app.core.tools.workspace import workspace_manager
import structlog

router = APIRouter()
logger = structlog.get_logger()
search_service = SearchService()

@router.post("/conversations", response_model=ConversationSchema)
async def create_conversation(
    *,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    conversation_in: ConversationCreate
) -> Any:
    conversation = Conversation(title=conversation_in.title, user_id=current_user.id)
    db.add(conversation)
    await db.commit()
    await db.refresh(conversation)
    return conversation

@router.get("/conversations", response_model=List[ConversationSchema])
async def read_conversations(
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    skip: int = 0,
    limit: int = 100,
) -> Any:
    result = await db.execute(
        select(Conversation)
        .where(Conversation.user_id == current_user.id, Conversation.is_deleted == False)
        .order_by(desc(Conversation.updated_at))
        .offset(skip)
        .limit(limit)
    )
    return result.scalars().all()

@router.get("/conversations/{id}/stream")
async def stream_conversation(
    request: Request,
    id: int,
    provider: Optional[str] = None,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user)
) -> EventSourceResponse:
    result = await db.execute(
        select(Conversation).where(Conversation.id == id, Conversation.user_id == current_user.id)
    )
    if not result.scalar_one_or_none():
        raise HTTPException(status_code=404, detail="Conversation not found")

    async def event_generator():
        async with SessionLocal() as session:
            msg_result = await session.execute(
                select(Message).where(Message.conversation_id == id, Message.role == "user")
                .order_by(desc(Message.created_at)).limit(1)
            )
            last_user_msg = msg_result.scalar_one_or_none()

        user_query = last_user_msg.content if last_user_msg else ""
        ai_provider = ModelRouter.route(user_query, provider)

        # 1. Clarification & Planning (Agent Layer)
        clarification = ClarificationManager.get_required_clarification(user_query)
        if clarification:
            yield {"event": "clarification_required", "data": clarification.model_dump_json()}
            await asyncio.sleep(1)
            yield {"event": "clarification_received", "data": json.dumps({"selection": clarification.options[0]})}

        # 2. Search Layer
        yield {"event": "search_start", "data": json.dumps({"query": user_query})}
        search_results = await search_service.execute_search(user_query)
        yield {"event": "search_sources", "data": json.dumps(search_results["sources"])}

        # 3. Tool Execution Layer
        if "execute" in user_query.lower() or "run" in user_query.lower():
            yield {"event": "tool_start", "data": json.dumps({"tool": "run_command", "input": "ls -la"})}
            workspace = workspace_manager.create_workspace(f"conv_{id}")
            async for chunk in SubprocessExecutor.run_with_streaming("ls -la", str(workspace)):
                yield {"event": "tool_output", "data": json.dumps({"output": chunk})}
            yield {"event": "tool_done", "data": json.dumps({"output": "Done"})}

        # 4. AI Stream Layer (Unified Provider)
        yield {"event": "message_start", "data": json.dumps({"conversation_id": id, "provider": ai_provider.provider_name})}

        accumulated = ""
        messages = PromptManager.format_messages([], user_query, search_results["context"])

        async for chunk in ai_provider.chat_stream(messages):
            if await request.is_disconnected(): return
            if chunk.text_delta:
                accumulated += chunk.text_delta
                yield {"event": "message_delta", "data": json.dumps({"delta": chunk.text_delta, "text": accumulated})}
            if chunk.is_done: break

        # 5. Finalize
        yield {"event": "citation", "data": json.dumps(CitationExtractor.extract(accumulated))}
        yield {"event": "followup_questions", "data": json.dumps(FollowUpGenerator.generate(user_query, accumulated))}

        async with SessionLocal() as session:
            session.add(Message(conversation_id=id, content=accumulated, role="assistant"))
            await session.execute(update(Conversation).where(Conversation.id == id).values(updated_at=func.now()))
            await session.commit()

        yield {"event": "search_done", "data": json.dumps({"text": accumulated})}

    return EventSourceResponse(event_generator())

@router.get("/conversations/{id}/messages", response_model=List[MessageSchema])
async def read_messages(
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    id: int,
    skip: int = 0,
    limit: int = 100,
) -> Any:
    conv_result = await db.execute(select(Conversation).where(Conversation.id == id, Conversation.user_id == current_user.id))
    if not conv_result.scalar_one_or_none(): raise HTTPException(status_code=404, detail="Conversation not found")
    result = await db.execute(select(Message).where(Message.conversation_id == id).order_by(Message.created_at).offset(skip).limit(limit))
    return result.scalars().all()
