from typing import AsyncGenerator
from app.services.ai.base import AIProvider, AIStreamChunk, AIResponse
import asyncio

class XAIProvider(AIProvider):
    @property
    def provider_name(self) -> str: return "xAI Grok"

    async def chat_stream(self, messages, tools=None, **kwargs) -> AsyncGenerator[AIStreamChunk, None]:
        yield AIStreamChunk(text_delta="[xAI Scaffold] Grokking...")
        yield AIStreamChunk(is_done=True)

    async def chat(self, messages, tools=None, **kwargs) -> AIResponse:
        return AIResponse(text="xAI full response")
