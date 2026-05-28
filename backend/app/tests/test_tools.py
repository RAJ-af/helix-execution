import pytest
from app.core.tools.security import SecurityFilter
from app.core.tools.registry import tool_registry

def test_security_filter():
    assert SecurityFilter.is_safe_command("ls -la") is True
    assert SecurityFilter.is_safe_command("rm -rf /") is False
    assert SecurityFilter.is_safe_command("sudo apt-get install") is False

def test_tool_registry():
    assert tool_registry.get_tool("run_command") is not None
    assert tool_registry.get_tool("read_file") is not None
    assert tool_registry.get_tool("non_existent") is None

@pytest.mark.asyncio
async def test_run_command_mock(tmp_path):
    # This is a bit complex to test without a full environment,
    # but we can test the logic if we mock the executor.
    pass
