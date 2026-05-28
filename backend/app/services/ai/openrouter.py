from typing import AsyncGenerator
from app.services.ai.base import AIProvider, AIStreamChunk, AIResponse
import asyncio

class OpenRouterProvider(AIProvider):
    @property
    def provider_name(self) -> str: return "OpenRouter"

    async def chat_stream(self, messages, tools=None, **kwargs) -> AsyncGenerator[AIStreamChunk, None]:
        text = "Greeting from OpenRouter. Accessing multiple models."
        for token in text.split(" "):
            yield AIStreamChunk(text_delta=token + " ")
            await asyncio.sleep(0.05)
        yield AIStreamChunk(is_done=True)

    async def chat(self, messages, tools=None, **kwargs) -> AIResponse:
        return AIResponse(text="OpenRouter full response")
