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
import structlog

router = APIRouter()
logger = structlog.get_logger()

@router.post("/conversations", response_model=ConversationSchema)
async def create_conversation(
    *,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    conversation_in: ConversationCreate
) -> Any:
    conversation = Conversation(
        title=conversation_in.title,
        user_id=current_user.id
    )
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

@router.get("/conversations/{id}", response_model=ConversationWithMessages)
async def read_conversation(
    *,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    id: int
) -> Any:
    result = await db.execute(
        select(Conversation)
        .where(Conversation.id == id, Conversation.user_id == current_user.id)
    )
    conversation = result.scalar_one_or_none()
    if not conversation:
        raise HTTPException(status_code=404, detail="Conversation not found")

    msg_result = await db.execute(
        select(Message)
        .where(Message.conversation_id == id)
        .order_by(Message.created_at)
    )
    conversation.messages = msg_result.scalars().all()
    return conversation

@router.patch("/conversations/{id}", response_model=ConversationSchema)
async def update_conversation(
    *,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    id: int,
    conversation_in: ConversationUpdate
) -> Any:
    result = await db.execute(
        select(Conversation)
        .where(Conversation.id == id, Conversation.user_id == current_user.id)
    )
    conversation = result.scalar_one_or_none()
    if not conversation:
        raise HTTPException(status_code=404, detail="Conversation not found")

    conversation.title = conversation_in.title
    await db.commit()
    await db.refresh(conversation)
    return conversation

@router.delete("/conversations/{id}")
async def delete_conversation(
    *,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    id: int
) -> Any:
    result = await db.execute(
        select(Conversation)
        .where(Conversation.id == id, Conversation.user_id == current_user.id)
    )
    conversation = result.scalar_one_or_none()
    if not conversation:
        raise HTTPException(status_code=404, detail="Conversation not found")

    conversation.is_deleted = True
    await db.commit()
    return {"status": "success"}

@router.post("/conversations/{id}/messages", response_model=List[MessageSchema])
async def create_message(
    *,
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    id: int,
    message_in: MessageCreate
) -> Any:
    result = await db.execute(
        select(Conversation)
        .where(Conversation.id == id, Conversation.user_id == current_user.id)
    )
    conversation = result.scalar_one_or_none()
    if not conversation:
        raise HTTPException(status_code=404, detail="Conversation not found")

    user_msg = Message(
        conversation_id=id,
        content=message_in.content,
        role="user"
    )
    db.add(user_msg)

    mock_response = f"I received your message: '{message_in.content}'. This is a mocked assistant response."
    assistant_msg = Message(
        conversation_id=id,
        content=mock_response,
        role="assistant"
    )
    db.add(assistant_msg)

    conversation.updated_at = func.now()
    await db.commit()
    await db.refresh(user_msg)
    await db.refresh(assistant_msg)

    return [user_msg, assistant_msg]

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
    conversation = result.scalar_one_or_none()
    if not conversation:
        raise HTTPException(status_code=404, detail="Conversation not found")

    async def event_generator():
        # Get the latest user message to "respond" to
        async with SessionLocal() as session:
            msg_result = await session.execute(
                select(Message)
                .where(Message.conversation_id == id, Message.role == "user")
                .order_by(desc(Message.created_at))
                .limit(1)
            )
            last_user_msg = msg_result.scalar_one_or_none()

        user_content = last_user_msg.content if last_user_msg else "your query"

        full_response = f"Streaming response for: {user_content}. This is a production-grade SSE implementation for the Helix AI MVP."
        tokens = full_response.split(" ")
        accumulated = ""

        yield {"event": "message_start", "data": json.dumps({"conversation_id": id})}

        for i, token in enumerate(tokens):
            if await request.is_disconnected():
                logger.info("client_disconnected", conversation_id=id)
                return

            chunk = token + (" " if i < len(tokens) - 1 else "")
            accumulated += chunk
            yield {"event": "message_delta", "data": json.dumps({"delta": chunk, "text": accumulated})}
            await asyncio.sleep(0.05) # Simulated latency

        # Keepalive example
        yield {"event": "keepalive", "data": ""}

        # Persist final assistant message
        async with SessionLocal() as session:
            assistant_msg = Message(
                conversation_id=id,
                content=accumulated,
                role="assistant"
            )
            session.add(assistant_msg)
            await session.execute(
                update(Conversation)
                .where(Conversation.id == id)
                .values(updated_at=func.now())
            )
            await session.commit()

        yield {"event": "message_done", "data": json.dumps({"text": accumulated})}

    return EventSourceResponse(event_generator())

@router.get("/conversations/{id}/messages", response_model=List[MessageSchema])
async def read_messages(
    db: AsyncSession = Depends(deps.get_db),
    current_user: User = Depends(deps.get_current_user),
    id: int,
    skip: int = 0,
    limit: int = 100,
) -> Any:
    conv_result = await db.execute(
        select(Conversation).where(Conversation.id == id, Conversation.user_id == current_user.id)
    )
    if not conv_result.scalar_one_or_none():
        raise HTTPException(status_code=404, detail="Conversation not found")

    result = await db.execute(
        select(Message)
        .where(Message.conversation_id == id)
        .order_by(Message.created_at)
        .offset(skip)
        .limit(limit)
    )
    return result.scalars().all()
