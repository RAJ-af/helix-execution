from typing import Optional, Any
from pydantic import BaseModel

class SseEvent(BaseModel):
    event: str
    data: Any
    id: Optional[str] = None
    retry: Optional[int] = None
