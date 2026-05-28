import re
from pathlib import Path
from typing import List

class SecurityFilter:
    BLOCKLIST = [
        r"rm\s+-rf", r"sudo\b", r"shutdown", r"reboot", r"mkfs", r"dd\b",
        r"curl.*\|\s*sh", r"wget.*\|\s*sh", r"mv\s+.*\/", r">/dev/null",
        r"chmod\s+777", r":\(\)\{.*\}", r"kill\s+-9", r"apt-get", r"pip\s+install"
    ]

    @staticmethod
    def is_safe_command(command: str) -> bool:
        for pattern in SecurityFilter.BLOCKLIST:
            if re.search(pattern, command, re.IGNORECASE):
                return False
        return True

    @staticmethod
    def is_within_workspace(path: str, workspace_path: Path) -> bool:
        try:
            full_path = Path(path).resolve()
            return str(full_path).startswith(str(workspace_path.resolve()))
        except Exception:
            return False

security_filter = SecurityFilter()
