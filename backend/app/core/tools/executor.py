import asyncio
import os
from typing import AsyncGenerator, Optional
from app.core.tools.base import ToolResult
from app.core.tools.security import security_filter

class SubprocessExecutor:
    @staticmethod
    async def run_with_streaming(
        command: str,
        cwd: str,
        timeout: int = 30
    ) -> AsyncGenerator[str, None]:
        if not security_filter.is_safe_command(command):
            yield "[SECURITY ERROR]: Command blocked."
            return

        process = await asyncio.create_subprocess_shell(
            command,
            stdout=asyncio.subprocess.PIPE,
            stderr=asyncio.subprocess.PIPE,
            cwd=cwd,
            env={"PATH": os.environ["PATH"]} # Restricted env
        )

        try:
            async def read_stream(stream, prefix):
                while True:
                    line = await stream.readline()
                    if not line:
                        break
                    yield line.decode()

            # For simplicity, we just merge them for streaming
            while True:
                stdout_line = await process.stdout.readline()
                if stdout_line:
                    yield stdout_line.decode()

                stderr_line = await process.stderr.readline()
                if stderr_line:
                    yield f"[ERR] {stderr_line.decode()}"

                if not stdout_line and not stderr_line and process.returncode is not None:
                    break

                await asyncio.sleep(0.01)

        except asyncio.TimeoutError:
            process.kill()
            yield "[TIMEOUT]: Command exceeded time limit."
