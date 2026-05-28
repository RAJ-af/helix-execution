import os
import shutil
import tempfile
from pathlib import Path

class WorkspaceManager:
    def __init__(self, base_path: str = "/tmp/helix_workspaces"):
        self.base_path = Path(base_path)
        self.base_path.mkdir(parents=True, exist_ok=True)

    def create_workspace(self, workspace_id: str) -> Path:
        workspace_dir = self.base_path / workspace_id
        workspace_dir.mkdir(exist_ok=True)
        return workspace_dir

    def get_workspace(self, workspace_id: str) -> Path:
        return self.base_path / workspace_id

    def cleanup_workspace(self, workspace_id: str):
        workspace_dir = self.base_path / workspace_id
        if workspace_dir.exists():
            shutil.rmtree(workspace_dir)

workspace_manager = WorkspaceManager()
