from abc import ABC, abstractmethod
from typing import List, Dict, Any
import httpx
from app.core.config import settings

class SearchResult:
    def __init__(self, title: str, url: str, content: str, score: float = 0.0):
        self.title = title
        self.url = url
        self.content = content
        self.score = score

class SearchProvider(ABC):
    @abstractmethod
    async def search(self, query: str, limit: int = 5) -> List[SearchResult]:
        pass

class TavilyProvider(SearchProvider):
    def __init__(self, api_key: str = "tvly-mock-key"):
        self.api_key = api_key
        self.base_url = "https://api.tavily.com/search"

    async def search(self, query: str, limit: int = 5) -> List[SearchResult]:
        # In a real app, use httpx to call Tavily
        # Mocking for MVP
        await asyncio.sleep(0.5)
        return [
            SearchResult(
                title=f"Source {i+1} for {query}",
                url=f"https://example.com/source{i+1}",
                content=f"This is a search snippet for source {i+1} discussing {query}. It contains relevant information grounded in facts.",
                score=0.9 - (i * 0.1)
            ) for i in range(limit)
        ]

import asyncio # Needed for mock sleep
