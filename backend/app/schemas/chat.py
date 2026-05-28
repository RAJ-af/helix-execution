from typing import List, Optional
from pydantic import BaseModel, ConfigDict
from datetime import datetime

class MessageBase(BaseModel):
    content: str
    role: str

class MessageCreate(MessageBase):
    pass

class Message(MessageBase):
    id: int
    conversation_id: int
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

class ConversationBase(BaseModel):
    title: Optional[str] = "New Conversation"

class ConversationCreate(ConversationBase):
    pass

class ConversationUpdate(BaseModel):
    title: str

class Conversation(ConversationBase):
    id: int
    user_id: int
    is_deleted: bool
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)

class ConversationWithMessages(Conversation):
    messages: List[Message] = []
