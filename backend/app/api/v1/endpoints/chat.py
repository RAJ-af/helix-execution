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
from app.models.task import Task, TaskStep
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

        # 1. Clarification Check
        clarification = ClarificationManager.get_required_clarification(user_query)
        if clarification:
            yield {"event": "clarification_required", "data": clarification.model_dump_json()}
            # For MVP, we stop here or simulate wait. Let's simulate a quick auto-resolve
            await asyncio.sleep(1)
            yield {"event": "clarification_received", "data": json.dumps({"selection": clarification.options[0]})}

        # 2. Planning
        yield {"event": "task_created", "data": json.dumps({"title": f"Agent Task: {user_query}", "conversation_id": id})}
        steps = TaskPlanner.plan(user_query)

        yield {"event": "task_updated", "data": json.dumps({"status": "running", "steps": steps})}

        for i, step_title in enumerate(steps):
            if await request.is_disconnected(): return

            yield {"event": "task_step_started", "data": json.dumps({"index": i, "title": step_title})}

            # Simulate execution
            if "search" in step_title.lower():
                yield {"event": "search_start", "data": json.dumps({"query": user_query})}
                await asyncio.sleep(0.5)
                yield {"event": "search_sources", "data": json.dumps([{"id": 1, "title": "Knowledge Base", "url": "https://kb.ai", "snippet": "Found info."}])}
            elif "read" in step_title.lower() or "analyze" in step_title.lower():
                yield {"event": "tool_start", "data": json.dumps({"tool": "run_command", "input": "ls -R"})}
                await asyncio.sleep(0.5)
                yield {"event": "tool_output", "data": json.dumps({"output": "src/\nmain.py\nrequirements.txt\n"})}
                yield {"event": "tool_done", "data": json.dumps({"output": "Done"})}

            await asyncio.sleep(0.5)
            yield {"event": "task_step_completed", "data": json.dumps({"index": i, "result": "Success"})}

        # 3. Final Response
        yield {"event": "task_completed", "data": json.dumps({"result": "Task finished successfully"})}

        full_response = f"I've completed the analysis of your request. Everything looks good. [1]"
        yield {"event": "message_start", "data": json.dumps({"conversation_id": id})}
        accumulated = ""
        for chunk in full_response.split(" "):
            text = chunk + " "
            accumulated += text
            yield {"event": "message_delta", "data": json.dumps({"delta": text, "text": accumulated})}
            await asyncio.sleep(0.05)

        yield {"event": "citation", "data": json.dumps([1])}
        yield {"event": "followup_questions", "data": json.dumps(["Run deeper analysis?", "Export report"])}

        async with SessionLocal() as session:
            session.add(Message(conversation_id=id, content=accumulated, role="assistant"))
            await session.execute(update(Conversation).where(Conversation.id == id).values(updated_at=func.now()))
            await session.commit()

        yield {"event": "search_done", "data": json.dumps({"text": accumulated})}

    return EventSourceResponse(event_generator())
