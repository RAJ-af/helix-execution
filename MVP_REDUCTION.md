# Helix MVP — Brutal Reduction Analysis

## The Problem

The architecture documents describe an ideal product. If built as specified, it would take 6+ months with 7 engineers. A startup with 1-3 people needs to ship something functional in 8-12 weeks.

This document identifies what must be cut, faked, or simplified.

---

## CUT: Not in MVP

### 1. Docker Sandbox — REPLACE with local subprocess (REMOTE)
- **Why cut**: Managing Docker containers, images, networking, seccomp profiles, and garbage collection is a full-time DevOps job. For MVP, the AI doesn't need real sandbox isolation.
- **Replacement**: A Python subprocess executor with basic path restriction. Run commands in a temp directory, limit with `timeout` and `ulimit`. Accept the security risk for MVP, add Docker later.
- **Saved**: ~4-6 weeks of engineering
- **Risk**: Lower security for MVP. Acceptable for closed beta.

### 2. Multi-model Routing — CUT
- **Why cut**: Claude Sonnet does everything well enough. Model routing adds latency, complexity, and a full testing matrix.
- **Replacement**: One model (Claude Sonnet). Period. Add model switching post-MVP.
- **Saved**: ~2 weeks
- **Risk**: None for MVP. Claude Sonnet is excellent.

### 3. Ollama / Local Models — CUT
- **Why cut**: Requires APK size increase, model download management, device testing matrix, and significantly more Android complexity.
- **Replacement**: Cloud-only for MVP. Add local inference post-MVP.
- **Saved**: ~3-4 weeks mobile engineering

### 4. Multi-agent Orchestration — CUT
- **Why cut**: Coordinating multiple agents, agent-to-agent communication, shared memory — this is research-level complexity.
- **Replacement**: Single agent that does planning + execution in one loop. Much simpler state machine.
- **Saved**: ~4 weeks

### 5. Scheduler / Cron System — CUT
- **Why cut**: Persistent schedules, cron parsing, Celery beat, retry logic, notification coordination — complex distributed systems problem.
- **Replacement**: Simple "run this later" delay queue. No persistence across restarts. No cron.
- **Saved**: ~2 weeks

### 6. Agent Memory / Vector DB — CUT
- **Why cut**: pgvector setup, embedding pipelines, semantic search, memory consolidation — significant infra complexity.
- **Replacement**: Store agent results as regular messages. Simple keyword search.
- **Saved**: ~1 week infra + 1 week backend

### 7. FCM Push Notifications — SIMPLIFY
- **Why cut**: FCM setup requires Firebase project, Google Services JSON, device token management, notification channels, deep linking.
- **Replacement**: Polling for MVP. Check agent status on app foreground or pull-to-refresh. Add FCM post-MVP.
- **Saved**: ~1 week mobile + backend

### 8. Research Mode (Multi-query) — SIMPLIFY
- **Why cut**: Query expansion, parallel search execution, result aggregation, thematic sectioning — complex pipeline.
- **Replacement**: Single search, generate comprehensive answer. If query is complex, the LLM can handle it in context.
- **Saved**: ~2 weeks

### 9. Workspace System — CUT
- **Why cut**: Workspace CRUD, navigation, organization hierarchy, archiving — standard but time-consuming.
- **Replacement**: Single "all conversations" list. No workspaces. Add post-MVP.
- **Saved**: ~1 week

### 10. Analytics / Crash Reporting — SIMPLIFY
- **Why cut**: Full analytics pipeline (events, funnels, dashboards) is a big effort.
- **Replacement**: Firebase Crashlytics only. No custom analytics. Server logs only.
- **Saved**: ~1 week

### 11. Tablet / Foldable Support — CUT
- **Why cut**: Responsive layouts, foldable continuity, different navigation patterns.
- **Replacement**: Phone-only MVP. Declare "phone optimized" on Play Store.
- **Saved**: ~2 weeks

### 12. Light Theme — CUT
- **Why cut**: Full color system, contrast testing, accessibility pass.
- **Replacement**: Dark mode only. Light mode post-MVP.
- **Saved**: ~1 week design + dev

---

## FAKE: Simulate Instead of Build

### 1. Tool Execution — Start with pseudo-tools
- **MVP approach**: Instead of a full tool framework, pre-define 3 tools:
  - `read_file(path)` — reads file from a whitelisted directory
  - `search_files(pattern)` — grep over a whitelisted directory
  - `run_command(cmd)` — subprocess with 5-second timeout
- Tool definitions are hardcoded. The LLM completes a JSON tool call, your backend parses it and executes the corresponding Python function. No tool registry, no plugin system, no extensibility.
- **Total tool system code**: ~200 lines Python, not 2000.

### 2. Streaming — Use Server-Sent Events, not WebSocket
- **MVP approach**: SSE over HTTP is dramatically simpler:
  - No connection management
  - No ping/pong
  - No reconnection logic
  - Works through any proxy
  - Every HTTP library supports it
- **Replacement**: `/api/v1/chat/{id}/stream` returns `text/event-stream`. Android client uses `OkHttp` streaming response body.
- **Saved**: ~2 weeks of WebSocket complexity
- **Switch to WebSocket** when you need bidirectional (tool output streaming, agent progress). Do SSE first.

### 3. Agent Clarification — Hardcoded MCQs
- **MVP approach**: Don't build a dynamic MCQ generation engine. Hardcode 3 clarification templates:
  - "Should I research A or B?"
  - "Do you want a brief or detailed answer?"
  - "Focus on practical examples or theory?"
- **Saved**: ~1 week of prompt engineering + validation

### 4. Permission System — Simple allowlist
- **MVP approach**: No permission dialogs, no "remember my choice", no granular controls. Tools are just executed. If you're worried about destructive commands, block them server-side with a regex list.
- **Saved**: ~1 week of UI + state management

### 5. Source Fetching — Use search API metadata only
- **MVP approach**: Don't fetch and parse HTML content from sources. Use the snippets returned by Tavily/Brave. If the snippet is insufficient, the answer will reflect that. Deep source extraction is post-MVP.
- **Saved**: ~2 weeks of content extraction pipeline

---

## SIMPLIFY: Reduce Scope Without Cutting

| Original | Simplified | Effort Saved |
|---|---|---|
| 10 REST endpoints per resource | 3-4 CRUD endpoints | 50% |
| Full WebSocket protocol | SSE for chat, WS for tools later | 60% |
| Agent framework with base classes | Single agent function that loops | 70% |
| Task execution engine | Sequential step runner | 60% |
| Permission manager | Deny-list for dangerous commands | 80% |
| SQLAlchemy + Alembic + models | Raw SQL or minimal ORM queries | 40% |
| 6 tool types | 3 tool types | 50% |
| 8 source ranking signals | 2 signals (relevance + freshness) | 70% |
| Full text search indexes | Simple ILIKE query | 90% |
| Audit logging system | Request logging only | 80% |

---

## What's Left After Reduction

### Core MVP (Weeks 1-8)

```
Backend (simplified):
- FastAPI app: main.py, auth.py, chat.py, search.py, tools.py
- ~15 files total
- 1 provider (Claude)
- 1 search provider (Tavily)
- SSE for streaming
- Subprocess executor for tools
- SQLite or simple Postgres (5 tables)

Android (simplified):
- MainActivity + NavHost
- 4 screens: Home, Chat, Settings, Agent
- 1 theme (dark)
- OkHttp SSE client
- Room DB (2 tables: conversations, messages)
- No WebSocket, no FCM, no local models

Total estimate: 8-10 weeks for 2 developers
```

### What User Gets

- ✅ Ask questions, get AI answers with citations
- ✅ Ask the AI to run commands / read files
- ✅ Ask the AI to research a topic autonomously
- ✅ See results streaming in real-time
- ✅ Conversation history
- ✅ Dark premium UI
