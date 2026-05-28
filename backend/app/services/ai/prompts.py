from typing import List, Dict

class PromptManager:
    @staticmethod
    def get_system_prompt() -> str:
        return (
            "You are Helix AI, a production-grade research and execution assistant. "
            "You have access to search tools and code execution. Use them wisely. "
            "Always ground your responses in provided search results and cite them as [1], [2], etc."
        )

    @staticmethod
    def format_messages(history: List[Dict[str, str]], query: str, context: str = "") -> List[Dict[str, str]]:
        messages = history.copy()
        if context:
            messages.append({"role": "system", "content": f"Relevant context from search: {context}"})
        messages.append({"role": "user", "content": query})
        return messages
