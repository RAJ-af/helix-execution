from abc import ABC, abstractmethod
from typing import AsyncGenerator, List, Dict, Any, Optional
from pydantic import BaseModel

class AIUsage(BaseModel):
    prompt_tokens: int = 0
    completion_tokens: int = 0
    total_tokens: int = 0

class AIToolCall(BaseModel):
    id: str
    name: str
    arguments: Dict[str, Any]

class AIResponse(BaseModel):
    text: str = ""
    tool_calls: List[AIToolCall] = []
    usage: AIUsage = AIUsage()

class AIStreamChunk(BaseModel):
    text_delta: Optional[str] = None
    tool_call_delta: Optional[AIToolCall] = None
    is_done: bool = False
    usage: Optional[AIUsage] = None

class AIProvider(ABC):
    @property
    @abstractmethod
    def provider_name(self) -> str:
        pass

    @abstractmethod
    async def chat_stream(
        self,
        messages: List[Dict[str, str]],
        tools: Optional[List[Dict[str, Any]]] = None,
        **kwargs
    ) -> AsyncGenerator[AIStreamChunk, None]:
        pass

    @abstractmethod
    async def chat(
        self,
        messages: List[Dict[str, str]],
        tools: Optional[List[Dict[str, Any]]] = None,
        **kwargs
    ) -> AIResponse:
        pass
