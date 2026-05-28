from typing import List, Optional
from pydantic import BaseModel, ConfigDict
from datetime import datetime

class TaskStepBase(BaseModel):
    title: str
    order: int
    status: str = "pending"

class TaskStep(TaskStepBase):
    id: int
    result: Optional[str] = None
    model_config = ConfigDict(from_attributes=True)

class TaskBase(BaseModel):
    title: str
    conversation_id: int

class TaskCreate(TaskBase):
    pass

class Task(TaskBase):
    id: int
    status: str
    steps: List[TaskStep] = []
    created_at: datetime
    model_config = ConfigDict(from_attributes=True)

class ClarificationRequest(BaseModel):
    question: str
    options: List[str]
