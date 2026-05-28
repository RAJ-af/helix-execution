from typing import AsyncGenerator
from app.services.ai.base import AIProvider, AIStreamChunk, AIResponse
import asyncio

class DeepSeekProvider(AIProvider):
    @property
    def provider_name(self) -> str: return "DeepSeek"

    async def chat_stream(self, messages, tools=None, **kwargs) -> AsyncGenerator[AIStreamChunk, None]:
        yield AIStreamChunk(text_delta="[DeepSeek Scaffold] Response...")
        yield AIStreamChunk(is_done=True)

    async def chat(self, messages, tools=None, **kwargs) -> AIResponse:
        return AIResponse(text="DeepSeek full response")
