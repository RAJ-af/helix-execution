from typing import AsyncGenerator
from app.services.ai.base import AIProvider, AIStreamChunk, AIResponse
import asyncio

class NvidiaNIMProvider(AIProvider):
    @property
    def provider_name(self) -> str: return "NVIDIA NIM"

    async def chat_stream(self, messages, tools=None, **kwargs) -> AsyncGenerator[AIStreamChunk, None]:
        yield AIStreamChunk(text_delta="[NVIDIA NIM Scaffold] GPU Accelerated Response...")
        yield AIStreamChunk(is_done=True)

    async def chat(self, messages, tools=None, **kwargs) -> AIResponse:
        return AIResponse(text="NVIDIA NIM full response")
