from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.sql import func
from app.db.base_class import Base

class Ping(Base):
    id = Column(Integer, primary_key=True, index=True)
    message = Column(String, default="pong")
    timestamp = Column(DateTime(timezone=True), server_default=func.now())
