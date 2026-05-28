# Helix MVP — Complete Task Breakdown

## Task Legend

```
Priority:   [CRITICAL] → Blocks everything | [IMPORTANT] → High value | [OPTIONAL] → Nice to have
Difficulty: [EASY] <1 day | [MEDIUM] 1-3 days | [HARD] 3-5 days | [VHARD] 5-10 days
Team:       [BACKEND] | [ANDROID] | [AI] | [DEVOPS] | [UI]
```

---

## PHASE 1: Foundation (Week 1-2)

### Task 1.1: Backend Project Scaffold
```
[CRITICAL] [EASY] [BACKEND]

Description:
Set up the Python FastAPI project with all boilerplate.

Subtasks:
- [ ] Initialize FastAPI app with uvicorn
- [ ] Create config management (pydantic-settings)
- [ ] Add health endpoint: GET /health
- [ ] Set up logging (structlog or json-logging)
- [ ] Create project folder structure
- [ ] Add CORS middleware
- [ ] Add error handlers
- [ ] Create requirements.txt (fastapi, uvicorn, httpx, etc.)

Files to create:
  /backend/app/main.py
  /backend/app/config.py
  /backend/app/dependencies.py
  /backend/requirements/base.txt
  /backend/requirements/dev.txt

Dependencies: Nothing
Blocks: All backend work
```

### Task 1.2: Database Setup
```
[CRITICAL] [EASY] [BACKEND]

Description:
Set up PostgreSQL and Redis connections.

Subtasks:
- [ ] Create SQLAlchemy async engine
- [ ] Write Docker Compose for postgres + redis
- [ ] Create base model class
- [ ] Add connection pooling config
- [ ] Write health check for DB + Redis
- [ ] Create initial migration

Files to create:
  /backend/app/database.py
  /backend/docker-compose.yml
  /docker/postgres/init.sql

Dependencies: Task 1.1
Blocks: Auth, Chat, Search, all data persistence
```

### Task 1.3: Android Project Scaffold
```
[CRITICAL] [EASY] [ANDROID]

Description:
Set up the Android project with Jetpack Compose and all dependencies.

Subtasks:
- [ ] Create new Android project
- [ ] Configure Gradle (version catalog, plugins)
- [ ] Set up Hilt DI modules
- [ ] Create theme (dark only)
- [ ] Set up Navigation Compose with route definitions
- [ ] Create NavHost with placeholder screens
- [ ] Add OkHttp + Ktor dependencies
- [ ] Add Room database dependencies
- [ ] Add DataStore dependencies
- [ ] Configure ProGuard/R8 rules

Dependencies: Nothing
Blocks: All Android work

Build.gradle dependencies:
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.navigation:navigation-compose")
  implementation("com.google.dagger:hilt-android")
  implementation("com.squareup.okhttp3:okhttp")
  implementation("androidx.room:room-runtime")
  implementation("androidx.room:room-ktx")
```

### Task 1.4: Docker Compose (Dev Environment)
```
[CRITICAL] [MEDIUM] [DEVOPS]

Description:
Full working dev environment with hot-reload.

Subtasks:
- [ ] Create Dockerfile for backend (dev + prod targets)
- [ ] Create docker-compose.yaml (web, postgres, redis)
- [ ] Add volume mounts for hot-reload
- [ ] Add health checks
- [ ] Add .env configuration
- [ ] Write setup script (./scripts/setup.sh)

Dependencies: Task 1.1, 1.2
Blocks: All development
```

---

## PHASE 2: Authentication (Week 2-3)

### Task 2.1: Auth API (Backend)
```
[CRITICAL] [MEDIUM] [BACKEND]

Description:
Full authentication system.

Subtasks:
- [ ] Create users table (SQLAlchemy model + migration)
- [ ] POST /api/v1/auth/signup
- [ ] POST /api/v1/auth/login (returns JWT)
- [ ] POST /api/v1/auth/refresh
- [ ] POST /api/v1/auth/logout (invalidate token)
- [ ] GET /api/v1/auth/me
- [ ] Password hashing with bcrypt
- [ ] JWT token creation + validation
- [ ] Auth dependency (get_current_user)
- [ ] Token blacklist on logout

Key files:
  /backend/app/models/user.py
  /backend/app/api/v1/auth.py
  /backend/app/core/auth.py
  /backend/app/core/security.py
  /backend/app/schemas/auth.py

Dependencies: Task 1.2
Blocks: All authenticated endpoints, Android auth UI
```

### Task 2.2: Auth UI (Android)
```
[CRITICAL] [MEDIUM] [ANDROID/UI]

Description:
Login and signup screens.

Subtasks:
- [ ] Welcome screen with app branding
- [ ] Login screen (email + password fields)
- [ ] Signup screen
- [ ] Form validation (email format, password length)
- [ ] Token storage (EncryptedSharedPreferences)
- [ ] Auth API client (OkHttp interceptors)
- [ ] Auto-login on app start (check stored token)
- [ ] Logout flow
- [ ] Loading states for auth operations
- [ ] Error handling (wrong password, network error)
- [ ] Navigation: Welcome → Login → Home

Screens:
  /app/src/main/java/com/helix/app/ui/screens/auth/WelcomeScreen.kt
  /app/src/main/java/com/helix/app/ui/screens/auth/LoginScreen.kt
  /app/src/main/java/com/helix/app/ui/screens/auth/LoginViewModel.kt
  /app/src/main/java/com/helix/app/ui/screens/auth/SignupScreen.kt

Dependencies: Task 2.1
Blocks: All authenticated screens
```

---

## PHASE 3: Chat & Streaming (Week 3-5)

### Task 3.1: Chat API (Backend)
```
[CRITICAL] [MEDIUM] [BACKEND]

Description:
Conversation and message CRUD API.

Subtasks:
- [ ] Create conversations table
- [ ] Create messages table
- [ ] POST /api/v1/conversations (create)
- [ ] GET /api/v1/conversations (list, paginated)
- [ ] GET /api/v1/conversations/{id} (get with messages)
- [ ] DELETE /api/v1/conversations/{id}
- [ ] PATCH /api/v1/conversations/{id} (rename)
- [ ] POST /api/v1/conversations/{id}/messages (send non-streaming)
- [ ] GET /api/v1/conversations/{id}/messages (paginated)

Key files:
  /backend/app/models/conversation.py
  /backend/app/models/message.py
  /backend/app/api/v1/chat.py
  /backend/app/services/chat_service.py
  /backend/app/schemas/chat.py

Dependencies: Task 1.2, 2.1
Blocks: Task 3.2, 3.3, 3.4
```

### Task 3.2: Chat UI (Android)
```
[CRITICAL] [HARD] [ANDROID/UI]

Description:
The main chat interface — the core UI of the app.

Subtasks:
- [ ] Conversation list screen
  - [ ] LazyColumn of conversation cards
  - [ ] Pull-to-refresh
  - [ ] Delete with swipe
  - [ ] Empty state
  - [ ] Loading skeleton

- [ ] Conversation screen
  - [ ] LazyColumn of message bubbles
  - [ ] User message (right-aligned, blue)
  - [ ] AI message (left-aligned, gray)
  - [ ] System message (centered, muted)
  - [ ] Auto-scroll to bottom
  - [ ] Scroll-to-bottom FAB

- [ ] Input bar
  - [ ] Multi-line text input (max 8 lines)
  - [ ] Send button
  - [ ] Model selector chip
  - [ ] "Research mode" toggle
  - [ ] Keyboard-aware (imePadding)

- [ ] Empty states
  - [ ] "Start a new conversation" with suggestion chips
  - [ ] Example prompts (3-4 chips)

Key files:
  /app/src/main/java/com/helix/app/ui/screens/chat/ConversationScreen.kt
  /app/src/main/java/com/helix/app/ui/screens/chat/ConversationViewModel.kt
  /app/src/main/java/com/helix/app/ui/screens/chat/MessageList.kt
  /app/src/main/java/com/helix/app/ui/screens/chat/InputBar.kt
  /app/src/main/java/com/helix/app/ui/components/message/UserMessage.kt
  /app/src/main/java/com/helix/app/ui/components/message/AiMessage.kt

Dependencies: Task 2.2
Blocks: Task 3.4 (streaming UI), all downstream UI
```

### Task 3.3: SSE Streaming (Backend)
```
[CRITICAL] [HARD] [BACKEND/AI]

Description:
Server-Sent Events endpoint for streaming AI responses.

Subtasks:
- [ ] GET /api/v1/conversations/{id}/stream (SSE)
- [ ] SSE event format: `data: {...}\n\n`
- [ ] Connect to Claude API with streaming
- [ ] Stream message_delta events
- [ ] Handle Claude tool-use requests
- [ ] Stream search_start / search_progress events
- [ ] Stream tool_start / tool_output events
- [ ] Stream message_done event
- [ ] Keepalive ping every 5 seconds
- [ ] Timeout handling (60s idle → close)
- [ ] Error events (rate limit, API error, timeout)
- [ ] Save completed message to database
- [ ] Cancelation support (client disconnects → stop generation)

Event types:
  message_start → { role: "assistant" }
  message_delta → { content: "partial text..." }
  message_done → { content: "full text", token_count: 123 }
  search_start → { query: "..." }
  search_progress → { message: "...", source_count: 5 }
  search_done → { sources: [...] }
  tool_start → { tool: "name", args: {...} }
  tool_output → { content: "stdout..." }
  tool_done → { exit_code: 0 }
  error → { code: "...", message: "..." }

Key files:
  /backend/app/api/v1/chat.py (stream endpoint)
  /backend/app/streaming/events.py
  /backend/app/streaming/serializers.py
  /backend/app/services/chat_service.py (stream method)

Dependencies: Task 3.1
Blocks: All streaming features (search, tools, agents)
```

### Task 3.4: Streaming UI (Android)
```
[CRITICAL] [MEDIUM] [ANDROID]

Description:
Display streaming AI responses in real-time.

Subtasks:
- [ ] SSE client using OkHttp (or dedicated SSE library)
- [ ] Parse SSE event stream into typed events
- [ ] Update message content incrementally (no flicker)
- [ ] Animated cursor while streaming
- [ ] Handle partial messages (app backgrounded → restore on foreground)
- [ ] Error state: "Connection lost" banner with retry
- [ ] Cancel in-flight request

Key files:
  /app/src/main/java/com/helix/app/data/remote/StreamingClient.kt
  /app/src/main/java/com/helix/app/ui/screens/chat/ConversationViewModel.kt (streaming state)

Dependencies: Task 3.2, 3.3
Blocks: Task 4.2, 5.3, 6.3 (all downstream streaming UIs)
```

### Task 3.5: Local Caching (Room)
```
[IMPORTANT] [MEDIUM] [ANDROID]

Description:
Cache conversations and messages locally.

Subtasks:
- [ ] Create conversation entity + DAO
- [ ] Create message entity + DAO
- [ ] Save messages on receive (append to Room)
- [ ] Load conversation from Room on open
- [ ] Offline indicator when no network
- [ ] Sync new messages on reconnect

Key files:
  /app/src/main/java/com/helix/app/data/local/db/HelixDatabase.kt
  /app/src/main/java/com/helix/app/data/local/db/dao/ConversationDao.kt
  /app/src/main/java/com/helix/app/data/local/db/dao/MessageDao.kt

Dependencies: Task 3.2
Blocks: Settings, history browsing
```

---

## PHASE 4: Search (Week 5-7)

### Task 4.1: Search Pipeline (Backend)
```
[CRITICAL] [HARD] [BACKEND/AI]

Description:
Web search integration with source fetching and answer generation.

Subtasks:
- [ ] Integrate Tavily search API
- [ ] POST /api/v1/search (single query → results + answer)
- [ ] Search → stream answer pipeline
- [ ] Source ranking (relevance + freshness)
- [ ] Context compression (fit sources in context window)
- [ ] Citation extraction (source quotes for inline [N])
- [ ] Follow-up question generation
- [ ] Search result deduplication
- [ ] Handle empty/no-results case

Key files:
  /backend/app/api/v1/search.py
  /backend/app/services/search_service.py
  /backend/app/services/search/providers.py
  /backend/app/services/search/ranking.py
  /backend/app/services/search/compression.py

Dependencies: Task 3.3
Blocks: Task 4.2
```

### Task 4.2: Search UI + Citations (Android)
```
[CRITICAL] [MEDIUM] [ANDROID/UI]

Description:
Display search results with inline citations and sources panel.

Subtasks:
- [ ] Render inline citations [1], [2] in AI messages
- [ ] Source card component (favicon, title, domain, snippet)
- [ ] Sources bottom sheet (swipe up from answer)
- [ ] Tap citation → scroll to source panel
- [ ] Tap source link → open in browser
- [ ] Follow-up suggestion chips below answer
- [ ] Research mode toggle in input bar

Key files:
  /app/src/main/java/com/helix/app/ui/components/search/SourceCard.kt
  /app/src/main/java/com/helix/app/ui/components/search/SourcesPanel.kt
  /app/src/main/java/com/helix/app/ui/components/search/CitationPill.kt
  /app/src/main/java/com/helix/app/ui/components/search/FollowUpChip.kt

Dependencies: Task 3.4, 4.1
Blocks: Nothing (feature complete here for search)
```

---

## PHASE 5: Tools (Week 7-9)

### Task 5.1: Tool Framework (Backend/AI)
```
[CRITICAL] [HARD] [AI/BACKEND]

Description:
Framework for defining, registering, and executing tools.

Subtasks:
- [ ] Define tool schema (name, description, parameters)
- [ ] Tool registry (register, get, list_for_model)
- [ ] Tool parameter validation
- [ ] Format tools as Claude function-calling schema
- [ ] Handle tool_use in Claude streaming response
- [ ] Execute tool → return result → continue AI conversation
- [ ] Tool execution loop (AI request → tool → AI response → tool → ...)
- [ ] Max tool call depth (5 iterations)
- [ ] Handle tool errors gracefully (report to AI, let it retry)

Implement 3 MVP tools (hardcoded, no Docker):

Tool 1: run_command
  - Executes shell command via subprocess
  - scoped to /tmp/helix_workspace/
  - blocklisted commands enforced
  - 10-second timeout

Tool 2: read_file
  - Reads file from whitelisted directory
  - Max 100KB per read
  - Line range support (offset + limit)

Tool 3: search_files (grep)
  - grep/ripgrep over whitelisted directory
  - Max 100 results

Key files:
  /backend/app/tools/base_tool.py
  /backend/app/tools/registry.py
  /backend/app/tools/terminal_tool.py
  /backend/app/tools/file_tool.py
  /backend/app/services/tool_service.py

Dependencies: Task 3.3
Blocks: Task 5.2, 5.3
```

### Task 5.2: Subprocess Executor (Backend)
```
[CRITICAL] [MEDIUM] [BACKEND]

Description:
The actual command execution engine (replaces Docker for MVP).

Subtasks:
- [ ] Create isolated workspace directory (/tmp/helix_workspace/{user_id}/)
- [ ] subprocess.run with timeout, cwd, env
- [ ] Capture stdout + stderr separately
- [ ] Blocklist enforcement (rm -rf, mkfs, dd, etc.)
- [ ] Run as nobody user
- [ ] ulimit enforcement (CPU time, memory, files)
- [ ] Stream output character-by-character
- [ ] Handle process kill on timeout
- [ ] Clean up workspace on conversation delete

Key files:
  /backend/app/sandbox/workspace_manager.py
  /backend/app/sandbox/executor.py
  /backend/app/sandbox/security.py

Dependencies: Task 5.1
Blocks: Task 5.3
```

### Task 5.3: Tool UI (Android)
```
[IMPORTANT] [MEDIUM] [ANDROID/UI]

Description:
Display tool execution and output in the chat.

Subtasks:
- [ ] Tool call card (tool name, args, status)
- [ ] Terminal output card (monospace, green-on-black)
- [ ] Real-time output streaming in card
- [ ] Expandable/collapsible tool output
- [ ] File content preview (syntax highlighted read-only)
- [ ] Tool error display (red output)
- [ ] Copy output button

Key files:
  /app/src/main/java/com/helix/app/ui/components/tool/ToolCallCard.kt
  /app/src/main/java/com/helix/app/ui/components/tool/TerminalOutput.kt
  /app/src/main/java/com/helix/app/ui/components/tool/FilePreview.kt

Dependencies: Task 3.4, 5.1
Blocks: Agent system (agents use tools)
```

---

## PHASE 6: Agents (Week 9-11)

### Task 6.1: Agent System (Backend/AI)
```
[IMPORTANT] [VHARD] [AI]

Description:
Autonomous task execution agent.

Subtasks:
- [ ] Agent prompt template (system message defining agent behavior)
- [ ] Task decomposition (take request → break into 3-8 steps)
- [ ] Agent execution loop:
  - [ ] Step 1: Plan → generate step list
  - [ ] Step 2: For each step → execute (search or tool)
  - [ ] Step 3: Check result → retry on failure (max 2x)
  - [ ] Step 4: After all steps → compile final answer
- [ ] Step execution using search + tool system
- [ ] Error handling + partial results
- [ ] Hard limits: max 10 steps, 5 min total, 3 retries/step
- [ ] Cancelation support
- [ ] Store agent execution state in database
- [ ] Agent progress events (step_start, step_done, step_progress)

Key files:
  /backend/app/agents/base_agent.py
  /backend/app/agents/research_agent.py
  /backend/app/agents/orchestrator.py
  /backend/app/services/agent_service.py
  /backend/app/api/v1/agents.py

Dependencies: Task 4.1 (search), 5.1 (tools)
Blocks: Task 6.2, 6.3
```

### Task 6.2: Agent CRUD API (Backend)
```
[IMPORTANT] [MEDIUM] [BACKEND]

Description:
API for creating, listing, and managing agents.

Subtasks:
- [ ] Create agents table
- [ ] POST /api/v1/agents (create)
- [ ] GET /api/v1/agents (list)
- [ ] DELETE /api/v1/agents/{id}
- [ ] POST /api/v1/agents/{id}/execute
- [ ] GET /api/v1/agents/executions (list, filterable)
- [ ] GET /api/v1/agents/executions/{id} (status)
- [ ] POST /api/v1/agents/executions/{id}/cancel

Dependencies: Task 6.1
Blocks: Task 6.3
```

### Task 6.3: Agent UI (Android)
```
[IMPORTANT] [HARD] [ANDROID/UI]

Description:
Agent management and execution progress screens.

Subtasks:
- [ ] Agent type selector (new agent creation)
- [ ] Agent list screen
- [ ] Agent execution screen
  - [ ] Step list with status indicators
  - [ ] Progress bar (overall completion %)
  - [ ] Current step output display
  - [ ] Expandable step details
  - [ ] Cancel button
  - [ ] Pause/resume button
- [ ] Agent history screen
- [ ] Execution result summary card

Key files:
  /app/src/main/java/com/helix/app/ui/screens/agent/AgentListScreen.kt
  /app/src/main/java/com/helix/app/ui/screens/agent/AgentExecutionScreen.kt
  /app/src/main/java/com/helix/app/ui/components/agent/AgentProgressView.kt
  /app/src/main/java/com/helix/app/ui/components/agent/StepProgress.kt

Dependencies: Task 5.3, 6.2
Blocks: Nothing (terminal feature)
```

---

## PHASE 7: Polish & Launch (Week 11-12)

### Task 7.1: Settings Screen (Android)
```
[IMPORTANT] [EASY] [ANDROID]

Subtasks:
- [ ] Account section (email, plan, logout)
- [ ] Appearance (theme toggle, font size)
- [ ] Model preferences (default model)
- [ ] About section (version, licenses)
- [ ] Delete account option

Dependencies: Task 3.5
```

### Task 7.2: Performance & Stability
```
[CRITICAL] [MEDIUM] [ALL]

Subtasks:
- [BACKEND] - Add Sentry error tracking
- [BACKEND] - Add request logging (method, path, duration, status)
- [BACKEND] - Profile slow endpoints (search pipeline likely bottleneck)
- [ANDROID] - Generate baseline profile
- [ANDROID] - Profile Compose recompositions
- [ANDROID] - Fix any ANR-prone paths
- [ANDROID] - Test with poor network (throttle in dev settings)
- [ANDROID] - Handle configuration changes (rotation, theme)
- [ALL] - End-to-end testing of critical path
```

### Task 7.3: Deployment
```
[CRITICAL] [MEDIUM] [DEVOPS]

Subtasks:
- [ ] Provision production server (single VPS, $40-80/mo)
- [ ] Configure nginx reverse proxy
- [ ] Set up SSL (Let's Encrypt)
- [ ] Deploy FastAPI behind gunicorn + uvicorn
- [ ] Set up PostgreSQL on server
- [ ] Set up Redis on server
- [ ] Configure environment variables
- [ ] Set up systemd services (auto-restart on crash)
- [ ] Configure firewall (ufw)
- [ ] Run initial DB migration
- [ ] Deploy app and verify health
```

### Task 7.4: Google Play Beta
```
[CRITICAL] [EASY] [ANDROID/DEVOPS]

Subtasks:
- [ ] Create Google Play Console account
- [ ] Prepare store listing (screenshots, description, privacy policy)
- [ ] Build signed release APK/AAB
- [ ] Set up internal test track
- [ ] Invite beta testers
- [ ] Set up Crashlytics
- [ ] Monitor first 48 hours closely
```

---

## Task Summary

```
Phase  │ Tasks │ Days  │ Critical │ Important │ Optional
────────┼───────┼───────┼──────────┼───────────┼─────────
1: Fnd  │ 4     │ 5     │ 4        │ 0         │ 0
2: Auth │ 2     │ 6     │ 2        │ 0         │ 0
3: Chat │ 5     │ 14    │ 5        │ 0         │ 0
4: Srch │ 2     │ 8     │ 2        │ 0         │ 0
5: Tool │ 3     │ 10    │ 2        │ 1         │ 0
6: Agnt │ 3     │ 10    │ 0        │ 3         │ 0
7: Ship │ 4     │ 8     │ 2        │ 2         │ 0

Total:  23 tasks, ~61 engineering days, 12 weeks
```

## Task Dependencies (Critical Path)

```
1.1 → 1.2 → 2.1 → 3.1 → 3.3 → 4.1 → 5.1 → 6.1
                                   ↓
                             5.2 → 5.3
                              ↓
1.3 → 2.2 → 3.2 → 3.4 → 4.2 → 5.3 → 6.3
       ↓
      3.5 (runs in parallel, not blocking)

Critical path: 1.1 → 1.2 → 2.1 → 3.1 → 3.3 → 4.1 → 5.1 → 6.1 → 6.2 → 7.2
Length: ~10 weeks of sequential work (parallelizable)
```
