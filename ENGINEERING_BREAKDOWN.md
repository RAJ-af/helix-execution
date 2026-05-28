# Helix MVP — Engineering Breakdown

## Architectural Simplifications

This document describes the SIMPLIFIED architecture for MVP — NOT the ideal architecture. Every simplification is intentional and comes with a note on when to fix it.

---

## Backend: Simplified Architecture

### File Structure

```
/backend/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI app, CORS, middleware
│   ├── config.py             # pydantic-settings (12 env vars, not 40)
│   ├── database.py           # SQLAlchemy async engine
│   │
│   ├── models/               # SQLAlchemy ORM models
│   │   ├── __init__.py
│   │   ├── user.py           # users table
│   │   ├── conversation.py   # conversations table
│   │   ├── message.py        # messages table
│   │   └── agent_execution.py # agent_executions table
│   │
│   ├── schemas/              # Pydantic request/response schemas
│   │   ├── __init__.py
│   │   ├── auth.py
│   │   ├── chat.py
│   │   └── agent.py
│   │
│   ├── api/
│   │   ├── __init__.py
│   │   ├── router.py         # Aggregate all routers
│   │   └── v1/
│   │       ├── __init__.py
│   │       ├── auth.py
│   │       ├── chat.py
│   │       ├── search.py
│   │       └── agents.py
│   │
│   ├── services/
│   │   ├── __init__.py
│   │   ├── auth_service.py
│   │   ├── chat_service.py   # Core AI orchestration
│   │   ├── search_service.py
│   │   └── agent_service.py
│   │
│   ├── core/
│   │   ├── __init__.py
│   │   ├── auth.py           # JWT helpers
│   │   └── security.py       # Password hashing
│   │
│   ├── tools/                # Tool system
│   │   ├── __init__.py
│   │   ├── registry.py       # Tool registry
│   │   ├── terminal.py       # run_command tool
│   │   ├── file_tools.py     # read_file, search_files
│   │   └── sandbox.py        # Subprocess executor + security
│   │
│   ├── providers/            # AI model providers
│   │   ├── __init__.py
│   │   ├── base.py           # Abstract provider
│   │   ├── claude.py         # Claude API (primary)
│   │   └── openai.py         # GPT fallback (or skip)
│   │
│   ├── agents/
│   │   ├── __init__.py
│   │   └── research_agent.py # Single agent implementation
│   │
│   └── utils/
│       ├── __init__.py
│       └── helpers.py        # Small utility functions
│
├── alembic/
│   └── versions/
│
├── tests/
│   ├── __init__.py
│   ├── test_chat.py
│   ├── test_search.py
│   └── test_tools.py
│
├── requirements.txt
├── Dockerfile
├── docker-compose.yml
└── .env.example

Total: ~35 files (not 100+)
```

### Key Simplifications

| Original Architecture | MVP Simplification | When to Fix |
|---|---|---|
| SQLAlchemy + 15 tables | SQLAlchemy + 4 tables | Add tables as needed |
| Alembic migrations | Single init script (no migrations) | Week 8+ when schema stabilizes |
| Celery worker pool | asyncio background tasks | When agent tasks take > 30s |
| Redis cache + queue | PostgreSQL only (skip Redis) | When DB load becomes an issue |
| WebSocket manager | SSE endpoint | When you need bidirectional streaming |
| 10 service files | 4 service files | As features grow |
| LangChain framework | Direct Claude API calls | If you need multi-provider routing |
| pydantic 40 env vars | 10-12 env vars | As you add providers |

---

## Android: Simplified Architecture

### File Structure

```
/app/src/main/java/com/helix/app/
├── HelixApplication.kt          # Hilt application
├── MainActivity.kt              # Single activity, nav host
│
├── di/
│   ├── AppModule.kt             # Hilt: DB, preferences
│   ├── NetworkModule.kt         # Hilt: OkHttp, API services
│   └── RepositoryModule.kt      # Hilt: repository bindings
│
├── ui/
│   ├── navigation/
│   │   ├── HelixNavHost.kt      # Navigation graph
│   │   └── Screen.kt            # Route definitions
│   │
│   ├── theme/
│   │   ├── Theme.kt             # HelixTheme (dark only)
│   │   ├── Color.kt             # Color tokens
│   │   └── Type.kt              # Typography
│   │
│   ├── components/              # Shared components
│   │   ├── MessageBubble.kt
│   │   ├── InputBar.kt
│   │   ├── SourceCard.kt
│   │   ├── TerminalOutput.kt
│   │   ├── AgentProgress.kt
│   │   └── LoadingIndicator.kt
│   │
│   ├── screens/
│   │   ├── auth/
│   │   │   ├── WelcomeScreen.kt
│   │   │   ├── LoginScreen.kt
│   │   │   ├── LoginViewModel.kt
│   │   │   └── SignupScreen.kt
│   │   │
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   │
│   │   ├── chat/
│   │   │   ├── ConversationScreen.kt
│   │   │   ├── ConversationViewModel.kt
│   │   │   └── ChatListScreen.kt
│   │   │
│   │   ├── agent/
│   │   │   ├── AgentExecutionScreen.kt
│   │   │   └── AgentExecutionViewModel.kt
│   │   │
│   │   └── settings/
│   │       └── SettingsScreen.kt
│   │
│   └── MainScreen.kt            # Bottom nav wrapper
│
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   ├── AuthApi.kt       # Retrofit interface
│   │   │   ├── ChatApi.kt
│   │   │   ├── SearchApi.kt
│   │   │   └── AgentApi.kt
│   │   │
│   │   ├── dto/                  # API response models
│   │   │   ├── AuthDtos.kt
│   │   │   ├── ChatDtos.kt
│   │   │   └── AgentDtos.kt
│   │   │
│   │   └── StreamingClient.kt   # SSE client
│   │
│   ├── local/
│   │   ├── HelixDatabase.kt
│   │   ├── dao/
│   │   │   ├── ConversationDao.kt
│   │   │   └── MessageDao.kt
│   │   └── entity/
│   │       ├── ConversationEntity.kt
│   │       └── MessageEntity.kt
│   │
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── ChatRepositoryImpl.kt
│       └── AgentRepositoryImpl.kt
│
├── domain/
│   └── model/
│       ├── Message.kt
│       ├── Conversation.kt
│       ├── Source.kt
│       └── AgentExecution.kt
│
└── util/
    ├── TokenManager.kt          # Secure token storage
    └── Extensions.kt

Total: ~45 files (not 150+)
```

### What We're NOT Building (Android)

| Architecture Doc Feature | MVP Decision | Rationale |
|---|---|---|
| UseCase layer | Skip — call repos from ViewModel | 2 layers is enough for MVP, UseCases add 30 files |
| Repository interfaces | Skip — concrete classes only | No need for polymorphism yet |
| Domain layer | Inline into data models | Not enough logic to justify separate layer |
| WorkManager tasks | Skip — no background work | No offline sync, no scheduling |
| FCM service | Skip — no push notifications | Polling is fine for beta |
| DataStore preferences | SharedPreferences | DataStore is overkill for 5 settings |
| Coil image loading | Skip — no images in MVP | Source favicons can wait |
| Navigation Compose | Single nav host, no nested graphs | 10 screens don't need nav graph hierarchy |

---

## Shared: SSE Stream Protocol

### Event Format

Every SSE event follows this format:

```
data: {"type": "event_type", "data": {...}}\n\n
```

### Event Types

```json
// Chat: text being generated
{"type": "delta", "data": {"content": "The answer is..."}}

// Chat: complete message
{"type": "message_done", "data": {"id": "msg_123", "content": "..."}}

// Search: start searching
{"type": "search_start", "data": {"query": "..."}}

// Search: progress update
{"type": "search_progress", "data": {"sources_found": 5, "message": "..."}}

// Search: found a source
{"type": "source_found", "data": {"index": 1, "title": "...", "url": "..."}}

// Search: complete
{"type": "search_done", "data": {"sources": [...]}}

// Tool: start execution
{"type": "tool_start", "data": {"name": "run_command", "args": {"command": "ls"}}}

// Tool: output chunk
{"type": "tool_output", "data": {"content": "file1.txt\nfile2.txt\n"}}

// Tool: complete
{"type": "tool_done", "data": {"exit_code": 0}}

// Agent: step progress
{"type": "agent_step", "data": {"step": 1, "total": 5, "status": "running", "description": "..."}}

// Agent: complete
{"type": "agent_done", "data": {"summary": "..."}}

// Error
{"type": "error", "data": {"code": "timeout", "message": "..."}}

// Heartbeat (keeps connection alive)
{"type": "ping"}
```

### Client Implementation (Kotlin)

```kotlin
class SseClient(private val okHttp: OkHttpClient) {

    fun stream(
        url: String,
        token: String,
        onEvent: (SseEvent) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ): Call {
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .header("Accept", "text/event-stream")
            .build()

        val call = okHttp.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val source = response.body?.source() ?: return
                val reader = BufferedReader(source.inputStream().reader())

                var line: String?
                val eventBuilder = StringBuilder()

                while (reader.readLine().also { line = it } != null) {
                    when {
                        line!!.startsWith("data: ") -> {
                            eventBuilder.append(line!!.removePrefix("data: "))
                        }
                        line!!.isEmpty() && eventBuilder.isNotEmpty() -> {
                            val json = eventBuilder.toString()
                            val event = Json.decodeFromString<SseEvent>(json)
                            onEvent(event)
                            eventBuilder.clear()
                        }
                    }
                }
                onComplete()
            }

            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }
        })
        return call
    }
}
```

### Server Implementation (Python)

```python
async def stream_response(conversation_id: str, user: User):
    async def event_generator():
        # Set up Claude streaming
        async with claude_client.stream_messages(
            model="claude-sonnet-4",
            messages=conversation.messages,
            system=SYSTEM_PROMPT,
        ) as stream:
            async for event in stream:
                if event.type == "content_block_delta":
                    yield f"data: {json.dumps({'type': 'delta', 'data': {'content': event.delta.text}})}\n\n"
                
                elif event.type == "content_block_start" and event.content_block.type == "tool_use":
                    yield f"data: {json.dumps({'type': 'tool_start', 'data': {'name': event.content_block.name, 'args': event.content_block.input}})}\n\n"
                    
                    # Execute tool
                    result = await execute_tool(event.content_block.name, event.content_block.input)
                    
                    yield f"data: {json.dumps({'type': 'tool_output', 'data': {'content': result.stdout}})}\n\n"
                    yield f"data: {json.dumps({'type': 'tool_done', 'data': {'exit_code': result.exit_code}})}\n\n"

            yield f"data: {json.dumps({'type': 'message_done', 'data': {'content': full_text}})}\n\n"

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no",
        }
    )
```

---

## Database: MVP Schema (4 Tables)

```sql
-- Only 4 tables for the MVP. No migrations.
-- Schema changes are manual ALTER TABLE until week 8.

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL DEFAULT 'New Conversation',
    model_id VARCHAR(50) NOT NULL DEFAULT 'claude-sonnet-4',
    is_archived BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,  -- 'user', 'assistant', 'system'
    content TEXT NOT NULL,
    metadata JSONB DEFAULT '{}',  -- sources, tool_calls, etc.
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE agent_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'queued',
    steps_plan JSONB,
    results JSONB,
    error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

-- Indexes
CREATE INDEX idx_conversations_user ON conversations(user_id);
CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_agent_executions_user ON agent_executions(user_id);
```

---

## AI Pipeline: Simplified Flow

```
User sends message
       │
       ▼
Backend receives message
       │
       ▼
Classify intent (if simple → direct answer, if complex → plan first)
       │
       ▼
Send to Claude with:
  - System prompt (instructions for search/tools)
  - Conversation history (last 20 messages)
  - Available tool definitions
       │
       ▼
Claude streams response
  ├── text → stream to client as "delta" events
  ├── tool_use → execute tool → send result back to Claude
  │              → Claude continues generating
  └── search needed → trigger search → inject results
                      → Claude continues generating
       │
       ▼
Save completed message to database
       │
       ▼
Stream "message_done" event
```

## Tools: Simplified Execution

```python
# The entire tool system in ~100 lines

import subprocess
import shlex
import os
from pathlib import Path

BLOCKED_COMMANDS = [
    "rm -rf /", "mkfs.", "dd if=", ":(){", "> /dev/",
    "wget ", "curl ",  # Block downloads (security)
]

WORKSPACE_BASE = Path("/tmp/helix_workspace")

async def execute_tool(name: str, args: dict) -> ToolResult:
    if name == "run_command":
        cmd = args["command"]
        
        # Security check
        for blocked in BLOCKED_COMMANDS:
            if blocked in cmd:
                return ToolResult(exit_code=-1, stderr=f"Blocked: {blocked}")
        
        # Create user workspace
        workspace = WORKSPACE_BASE / args.get("user_id", "default")
        workspace.mkdir(parents=True, exist_ok=True)
        
        # Execute with restrictions
        try:
            result = subprocess.run(
                ["/bin/bash", "-c", cmd],
                cwd=str(workspace),
                capture_output=True,
                text=True,
                timeout=10,
                env={},
                preexec_fn=lambda: os.nice(10),  # Low priority
            )
            return ToolResult(
                exit_code=result.returncode,
                stdout=result.stdout[-10000:],  # Max 10KB
                stderr=result.stderr[-1000:],
            )
        except subprocess.TimeoutExpired:
            return ToolResult(exit_code=-1, stderr="Command timed out (10s)")
    
    elif name == "read_file":
        path = args["path"]
        # Path traversal check
        full_path = (WORKSPACE_BASE / path).resolve()
        if not str(full_path).startswith(str(WORKSPACE_BASE)):
            return ToolResult(exit_code=-1, stderr="Access denied")
        if not full_path.exists():
            return ToolResult(exit_code=-1, stderr="File not found")
        content = full_path.read_text()[:100_000]  # Max 100KB
        return ToolResult(exit_code=0, stdout=content)
    
    elif name == "search_files":
        pattern = args["pattern"]
        result = subprocess.run(
            ["grep", "-r", "-n", pattern, str(WORKSPACE_BASE)],
            capture_output=True, text=True, timeout=5
        )
        lines = result.stdout.split("\n")[:100]
        return ToolResult(exit_code=0, stdout="\n".join(lines))
```

---

## Cost of Simplifications

| Simplification | Effort Saved | Cost/Drawback |
|---|---|---|
| Subprocess instead of Docker | 4 weeks | No isolation — security risk for multi-tenant |
| SSE instead of WebSocket | 2 weeks | No server push (tool output, agent progress) |
| Single model (Claude) | 2 weeks | Vendor lock-in risk |
| No agent memory | 1 week | Agents forget past executions |
| No migrations | 1 week | Schema changes require downtime |
| No tests | 2 weeks | Regressions will happen |
| No CI/CD | 1 week | Manual deployment risk |
| 4 DB tables | 1 week | Adding tables later is easy |
| Skip Workspaces | 1 week | Flat conversation list |
| No use cases layer | 1 week | ViewModels bigger but manageable |

**Total saved: ~16 weeks of engineering**  
**Total debt: ~4 weeks of post-MVP cleanup**  
**Net: 12 weeks to MVP instead of 28 weeks**
