import json
from typing import AsyncGenerator, List, Dict, Any, Optional
from app.services.ai.base import AIProvider, AIStreamChunk, AIResponse, AIToolCall, AIUsage

class ClaudeProvider(AIProvider):
    @property
    def provider_name(self) -> str: return "Anthropic Claude"

    async def chat_stream(self, messages, tools=None, **kwargs) -> AsyncGenerator[AIStreamChunk, None]:
        # Mocking real Claude stream
        text = "Hello! I am Claude. I can help you with search and tool execution."
        for token in text.split(" "):
            yield AIStreamChunk(text_delta=token + " ")
            await asyncio.sleep(0.05)
        yield AIStreamChunk(is_done=True, usage=AIUsage(prompt_tokens=10, completion_tokens=20, total_tokens=30))

    async def chat(self, messages, tools=None, **kwargs) -> AIResponse:
        return AIResponse(text="Claude's full response")

import asyncio
