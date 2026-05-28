from app.services.ai.registry import provider_registry
from app.services.ai.base import AIProvider

class ModelRouter:
    @staticmethod
    def route(query: str, preferred_provider: str = None) -> AIProvider:
        if preferred_provider and preferred_provider in provider_registry.providers:
            return provider_registry.get_provider(preferred_provider)

        # Simple heuristic routing
        if "code" in query.lower() or "execute" in query.lower():
            return provider_registry.get_provider("claude")
        elif "fast" in query.lower() or "quick" in query.lower():
            return provider_registry.get_provider("gemini")

        return provider_registry.get_provider("claude")
