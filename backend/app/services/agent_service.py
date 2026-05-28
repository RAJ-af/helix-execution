import asyncio
from typing import List, Dict, Any
from app.models.task import Task, TaskStep
from app.schemas.task import ClarificationRequest

class TaskPlanner:
    @staticmethod
    def plan(query: str) -> List[str]:
        # Template-driven planning for MVP
        if "analyze" in query.lower():
            return ["Search project files", "Read configuration", "Analyze structure", "Generate summary"]
        elif "fix" in query.lower():
            return ["Locate bug", "Read source code", "Apply fix", "Verify fix"]
        else:
            return ["Research topic", "Collect sources", "Synthesize information"]

class AgentService:
    @staticmethod
    async def run_task_loop(task_id: int, query: str):
        # This will be used in the SSE generator
        pass

class ClarificationManager:
    @staticmethod
    def get_required_clarification(query: str) -> Optional[ClarificationRequest]:
        if "analyze" in query.lower() and "detail" not in query.lower():
            return ClarificationRequest(
                question="Would you like a brief or detailed analysis?",
                options=["Brief", "Detailed", "Technical Deep-dive"]
            )
        return None

from typing import Optional
