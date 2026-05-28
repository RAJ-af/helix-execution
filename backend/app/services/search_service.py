from typing import List, Dict, Any
from app.services.search_provider import SearchProvider, SearchResult, TavilyProvider

class SearchService:
    def __init__(self, provider: SearchProvider = TavilyProvider()):
        self.provider = provider

    async def execute_search(self, query: str) -> Dict[str, Any]:
        results = await self.provider.search(query)

        # Normalize and Rank
        sorted_results = sorted(results, key=lambda x: x.score, reverse=True)

        # Format for context
        context = "\n\n".join([f"Source {i+1} ({r.url}): {r.content}" for i, r in enumerate(sorted_results)])

        return {
            "sources": [
                {"id": i+1, "title": r.title, "url": r.url, "snippet": r.content}
                for i, r in enumerate(sorted_results)
            ],
            "context": context
        }

class CitationExtractor:
    @staticmethod
    def extract(text: str) -> List[int]:
        # Simple extraction of [1], [2] style citations
        import re
        citations = re.findall(r'\[(\d+)\]', text)
        return sorted(list(set([int(c) for c in citations])))

class FollowUpGenerator:
    @staticmethod
    def generate(query: str, response: str) -> List[str]:
        # Simple mock generator
        return [
            f"Can you tell me more about {query}?",
            f"How does this relate to other topics?",
            "Give me a detailed summary."
        ]
