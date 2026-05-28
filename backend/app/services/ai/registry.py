from typing import Dict
from app.services.ai.base import AIProvider
from app.services.ai.claude import ClaudeProvider
from app.services.ai.gemini import GeminiProvider
from app.services.ai.openrouter import OpenRouterProvider
from app.services.ai.openai import OpenAIProvider
from app.services.ai.deepseek import DeepSeekProvider
from app.services.ai.xai import XAIProvider
from app.services.ai.ollama import OllamaProvider
from app.services.ai.nvidia import NvidiaNIMProvider

class ProviderRegistry:
    def __init__(self):
        self.providers: Dict[str, AIProvider] = {
            "claude": ClaudeProvider(),
            "gemini": GeminiProvider(),
            "openrouter": OpenRouterProvider(),
            "openai": OpenAIProvider(),
            "deepseek": DeepSeekProvider(),
            "xai": XAIProvider(),
            "ollama": OllamaProvider(),
            "nvidia": NvidiaNIMProvider()
        }

    def get_provider(self, name: str) -> AIProvider:
        return self.providers.get(name, self.providers["claude"])

provider_registry = ProviderRegistry()
