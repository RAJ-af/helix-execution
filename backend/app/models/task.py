from sqlalchemy import Column, Integer, String, ForeignKey, DateTime, Text, JSON
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.db.base_class import Base

class Task(Base):
    id = Column(Integer, primary_key=True, index=True)
    conversation_id = Column(Integer, ForeignKey("conversation.id"), nullable=False)
    title = Column(String, nullable=False)
    status = Column(String, default="pending") # pending, planning, running, waiting_for_input, completed, failed, cancelled
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    steps = relationship("TaskStep", back_populates="task", cascade="all, delete-orphan")

class TaskStep(Base):
    id = Column(Integer, primary_key=True, index=True)
    task_id = Column(Integer, ForeignKey("task.id"), nullable=False)
    title = Column(String, nullable=False)
    status = Column(String, default="pending") # pending, running, completed, failed, skipped
    order = Column(Integer, nullable=False)
    result = Column(Text, nullable=True)

    task = relationship("Task", back_populates="steps")
