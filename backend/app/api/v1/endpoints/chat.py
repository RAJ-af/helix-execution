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

        # 1. Search Simulation
        yield {"event": "search_start", "data": json.dumps({"query": user_query})}
        await asyncio.sleep(0.5)
        sources = [{"id": 1, "title": "Helix Documentation", "url": "https://helix.ai/docs", "snippet": "Tool execution is secure."}]
        yield {"event": "search_sources", "data": json.dumps(sources)}

        # 2. Tool Execution Simulation (Claude Code style)
        command = "ls -la"
        yield {"event": "tool_start", "data": json.dumps({"tool": "run_command", "input": command})}

        workspace = workspace_manager.create_workspace(f"conv_{id}")
        output_acc = ""
        async for chunk in SubprocessExecutor.run_with_streaming(command, str(workspace)):
            output_acc += chunk
            yield {"event": "tool_output", "data": json.dumps({"output": chunk})}
            await asyncio.sleep(0.05)

        yield {"event": "tool_done", "data": json.dumps({"output": output_acc})}

        # 3. Response Streaming
        full_response = f"I executed '{command}' in your workspace. The output shows the files. Based on my search, everything is configured correctly [1]."
        yield {"event": "message_start", "data": json.dumps({"conversation_id": id})}

        accumulated = ""
        for chunk in full_response.split(" "):
            if await request.is_disconnected(): return
            text = chunk + " "
            accumulated += text
            yield {"event": "message_delta", "data": json.dumps({"delta": text, "text": accumulated})}
            await asyncio.sleep(0.05)

        # 4. Finalize
        yield {"event": "citation", "data": json.dumps([1])}
        yield {"event": "followup_questions", "data": json.dumps(["How do I run more tools?", "Show me the logs"])}

        async with SessionLocal() as session:
            session.add(Message(conversation_id=id, content=accumulated, role="assistant"))
            await session.execute(update(Conversation).where(Conversation.id == id).values(updated_at=func.now()))
            await session.commit()

        yield {"event": "search_done", "data": json.dumps({"text": accumulated})}

    return EventSourceResponse(event_generator())
