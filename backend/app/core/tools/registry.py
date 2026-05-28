from typing import Dict, Type
from app.core.tools.base import BaseTool
from app.core.tools.workspace import workspace_manager
from app.core.tools.executor import SubprocessExecutor
from app.core.tools.security import security_filter
import os

class RunCommandTool(BaseTool):
    @property
    def name(self) -> str: return "run_command"
    @property
    def description(self) -> str: return "Execute a shell command in the workspace"

    async def execute(self, command: str, workspace_id: str = "default") -> str:
        workspace = workspace_manager.get_workspace(workspace_id)
        output = []
        async for chunk in SubprocessExecutor.run_with_streaming(command, str(workspace)):
            output.append(chunk)
        return "".join(output)

class ReadFileTool(BaseTool):
    @property
    def name(self) -> str: return "read_file"
    @property
    def description(self) -> str: return "Read content of a file"

    async def execute(self, path: str, workspace_id: str = "default") -> str:
        workspace = workspace_manager.get_workspace(workspace_id)
        full_path = workspace / path
        if not security_filter.is_within_workspace(full_path, workspace):
            return "Error: Path outside workspace"
        try:
            with open(full_path, 'r') as f:
                return f.read()
        except Exception as e:
            return f"Error reading file: {str(e)}"

class SearchFilesTool(BaseTool):
    @property
    def name(self) -> str: return "search_files"
    @property
    def description(self) -> str: return "Search for files in workspace"

    async def execute(self, pattern: str, workspace_id: str = "default") -> str:
        workspace = workspace_manager.get_workspace(workspace_id)
        output = []
        async for chunk in SubprocessExecutor.run_with_streaming(f"find . -name '*{pattern}*'", str(workspace)):
            output.append(chunk)
        return "".join(output)

class ToolRegistry:
    def __init__(self):
        self.tools: Dict[str, BaseTool] = {
            "run_command": RunCommandTool(),
            "read_file": ReadFileTool(),
            "search_files": SearchFilesTool()
        }

    def get_tool(self, name: str) -> BaseTool:
        return self.tools.get(name)

tool_registry = ToolRegistry()
