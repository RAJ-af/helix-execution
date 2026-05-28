from typing import AsyncGenerator
from app.services.ai.base import AIProvider, AIStreamChunk, AIResponse, AIUsage
import asyncio

class GeminiProvider(AIProvider):
    @property
    def provider_name(self) -> str: return "Google Gemini"

    async def chat_stream(self, messages, tools=None, **kwargs) -> AsyncGenerator[AIStreamChunk, None]:
        text = "Hi, I'm Gemini. I'm optimized for fast search responses."
        for token in text.split(" "):
            yield AIStreamChunk(text_delta=token + " ")
            await asyncio.sleep(0.03)
        yield AIStreamChunk(is_done=True)

    async def chat(self, messages, tools=None, **kwargs) -> AIResponse:
        return AIResponse(text="Gemini's full response")
