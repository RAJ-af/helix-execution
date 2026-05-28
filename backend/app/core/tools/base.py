from abc import ABC, abstractmethod
from typing import Any, Dict, Optional
from pydantic import BaseModel

class ToolResult(BaseModel):
    stdout: str = ""
    stderr: str = ""
    exit_code: int = 0
    error: Optional[str] = None
    metadata: Dict[str, Any] = {}

class BaseTool(ABC):
    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @property
    @abstractmethod
    def description(self) -> str:
        pass

    @abstractmethod
    async def execute(self, **kwargs) -> ToolResult:
        pass
