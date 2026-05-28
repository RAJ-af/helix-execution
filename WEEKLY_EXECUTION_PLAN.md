# Helix MVP — Week-by-Week Execution Plan

## How to Use This Plan

- Each week has **deliverables** — working software, not tasks in progress
- Each week ends with a **checkpoint** — can you demo this to someone?
- If a week slips, **cut scope** — do not extend the timeline
- **Parallel tracks** (Backend + Android) run simultaneously
- The **AI/FS** track supports whichever side needs help

---

## Week 1: Scaffold & Connect

### Backend Track
```
Goal: Running FastAPI app with database

Mon: Initialize FastAPI project, folder structure, config
Tue: Add health endpoint, CORS, error handlers
Wed: Docker Compose (postgres + redis), SQLAlchemy setup
Thu: Dockerfile for development (hot-reload)
Fri: Verify: docker compose up → health endpoint returns OK

Files created: ~10
Key output: GET /health returns {"status": "ok"}
```

### Android Track
```
Goal: Running Android app with navigation

Mon: Create project, configure Gradle, apply Hilt
Tue: Set up theme (dark mode colors + typography)
Wed: Define routes, create NavHost with placeholder screens
Thu: Set up Hilt modules (network, database)
Fri: Verify: app launches, navigates between empty screens

Files created: ~15
Key output: App compiles and shows 4 empty tabs
```

### Checkpoint: End of Week 1

```
Can you:
- [ ] Run `docker compose up` and visit /health?
- [ ] Run the Android app and see the navigation?
- [ ] Both developers have their environments working?

If no → stop and fix. Everything depends on this.
```

---

## Week 2: Auth Backend

### Backend Track
```
Goal: Working auth API

Mon: Create users table (SQLAlchemy model + migration)
Tue: POST /auth/signup + POST /auth/login
Wed: JWT token creation + validation middleware
Thu: POST /auth/refresh + GET /auth/me
Fri: Password hashing + error handling + token blacklist

Key output: curl -X POST /auth/signup works end-to-end
```

### Android Track
```
Goal: Welcome screen + API client setup

Mon: Welcome screen with app branding
Tue: OkHttp client setup with auth interceptor
Wed: API service interfaces (Retrofit/Ktor)
Thu: Login screen with email/password fields
Fri: Token storage (EncryptedSharedPreferences)

Key output: Login screen UI renders
```

### Checkpoint: End of Week 2

```
Can you:
- [ ] Register a new user via API?
- [ ] Login and receive a JWT?
- [ ] Android API client initialized?

Demo: curl signup → curl login → curl /auth/me
```

---

## Week 3: Auth UI + Chat Backend

### Backend Track
```
Goal: Chat API with conversation management

Mon: Conversations table + CRUD API
Tue: Messages table + message API
Wed: Simple Claude API integration (non-streaming)
Thu: POST /chat → send message → get AI response
Fri: Conversation listing + pagination

Key output: POST /chat returns a complete AI response
```

### Android Track
```
Goal: Complete auth flow + chat list

Mon: Connect Login screen to backend (working login)
Tue: Signup screen + validation
Wed: Auto-login on app start (check stored token)
Thu: Conversation list screen (fetch from API)
Fri: Connect login → home navigation flow

Key output: Login → see conversation list
```

### Checkpoint: End of Week 3

```
Can you:
- [ ] Register and login from the Android app?
- [ ] See an empty conversation list?
- [ ] POST a message and get an AI response?

Demo: Login on phone → send message → see AI response
```

---

## Week 4: Streaming Backend

### Backend Track
```
Goal: SSE streaming for AI responses + tool-use loop

Mon: Claude streaming API integration (content_block_delta)
Tue: SSE endpoint: GET /chat/{id}/stream
Wed: Handle Claude tool_use in streaming response
Thu: Basic tool execution loop (AI → tool → AI)
Fri: Save messages to DB on completion

Key output: curl stream endpoint shows SSE events
```

### Android Track
```
Goal: Chat screen with message bubbles

Mon: Chat screen scaffold (LazyColumn of messages)
Tue: User message bubble (right-aligned)
Wed: AI message bubble (left-aligned) with markdown
Thu: Input bar (multi-line + send button)
Fri: Connect chat screen to backend (non-streaming first)

Key output: Type message → send → see response in chat
```

### Checkpoint: End of Week 4

```
Demo: Send a message → see AI response in chat
                          ↑
                This is the first "wow" moment
                Everything before was foundation
```

---

## Week 5: Streaming UI + Search Prep

### Backend Track
```
Goal: Search pipeline (without answer generation)

Mon: Integrate Tavily search API
Tue: Source ranking (relevance score + freshness)
Wed: Source context compression (fit sources into prompt)
Thu: Search result deduplication
Fri: Link search → streaming answer pipeline

Key output: Request with search intent returns sources
```

### Android Track
```
Goal: Streaming response display

Mon: SSE client implementation (OkHttp streaming)
Tue: Parse SSE events into typed objects
Wed: Incremental text update (no flicker!)
Thu: Animated cursor while AI types
Fri: Handle connection errors + reconnect

Key output: Messages appear character-by-character
```

### Checkpoint: End of Week 5

```
Can you:
- [ ] See AI response streaming in real-time?
- [ ] Backend search pipeline returns results?

Demo: Ask a question → watch text appear word-by-word
```

---

## Week 6: Search + Citations

### Backend Track
```
Goal: Complete search → answer pipeline

Mon: Search → stream answer (with context injection)
Tue: Citation extraction + inline [N] markers
Wed: Follow-up question generation
Thu: Handle edge cases (no results, low confidence)
Fri: Search response optimization + caching

Key output: Search query returns cited answer
```

### Android Track
```
Goal: Search results UI + citations

Mon: Source card component (title, domain, snippet)
Tue: Sources bottom sheet (swipe up)
Wed: Inline citation rendering [1], [2] etc.
Thu: Follow-up suggestion chips
Fri: Research mode toggle in input bar

Key output: Search results with tappable citations
```

### Checkpoint: End of Week 6

```
Demo: Ask "latest Kotlin trends" → see cited answer
      → tap source → open in browser
      → tap follow-up → continue conversation

This is the Perplexity experience on Android.
```

---

## Week 7: Tool System

### Backend Track
```
Goal: Working tool execution (no Docker)

Mon: Tool registry with 3 tools (command, read, search)
Tue: Subprocess executor with security validation
Wed: Tool parameter validation + error handling
Thu: Integrate tool calls into streaming response
Fri: Tool execution loop (max 5 iterations)

Key output: Ask "list files" → AI runs ls → returns output
```

### Android Track
```
Goal: Tool output display

Mon: Tool call card component (name + args)
Tue: Terminal output card (monospace, green text)
Wed: Real-time output streaming in expandable card
Thu: Tool execution in streaming pipeline
Fri: Error display for failed tools

Key output: Tool output shown in chat
```

### Checkpoint: End of Week 7

```
Demo: Ask "find all .py files" → see AI run find command
      → see output in terminal-style card → AI summarizes
```

---

## Week 8: Tool Integration + Polish

### Backend Track
```
Goal: Polished tool execution

Mon: File read tool (whitelisted paths)
Tue: File search tool (grep)
Wed: Tool error recovery (AI retries on failure)
Thu: Tool output truncation (max 10KB)
Fri: Tool execution logging + monitoring

Key output: All 3 tools working reliably
```

### Android Track
```
Goal: Complete tool UX

Mon: File preview component (syntax highlighted)
Tue: Expandable/collapsible tool output cards
Wed: Copy output button
Thu: Tool error states
Fri: Polish: animations, transitions, loading states

Key output: Polished tool execution UX
```

### Checkpoint: End of Week 8

```
Demo: "Analyze this Python script for bugs"
      → AI reads file
      → runs pylint
      → reviews output
      → gives recommendations
```

---

## Week 9: Agent System

### Backend Track
```
Goal: Basic autonomous agent

Mon: Agent system prompt + execution loop
Tue: Task decomposition (plan → step list)
Wed: Step execution (search/tool per step)
Thu: Step result collection + final compilation
Fri: Agent execution limits (max 10 steps, 5 min)

Key output: POST /agent/execute with task returns steps + result
```

### Android Track
```
Goal: Agent execution UI (basic)

Mon: Agent type selector screen
Tue: Agent execution screen scaffold
Wed: Step list with status indicators
Thu: Progress bar + current step display
Fri: Cancel button + error handling

Key output: Start agent → see steps progressing
```

### Checkpoint: End of Week 9

```
Demo: "Research microservice patterns"
      → Agent plans 5 steps
      → Each step searches web
      → Compiles final report
      → All shown in progress UI
```

---

## Week 10: Agent Integration + Settings

### Backend Track
```
Goal: Agent history + error resilience

Mon: Agent execution persistence (database)
Tue: Agent cancelation handling
Wed: Agent timeout handling
Thu: Agent retry on transient errors
Fri: Agent API polish + error responses

Key output: Agents survive server restart
```

### Android Track
```
Goal: Settings + agent history

Mon: Agent history screen (past executions)
Tue: Settings screen (account, preferences)
Wed: Appearance settings (font size)
Thu: About screen (version, licenses)
Fri: Logout + account management

Key output: Settings screens functional
```

### Checkpoint: End of Week 10

```
All features working:
  ☐ Auth (signup, login, logout)
  ☐ Chat (messages, history)
  ☐ Streaming (real-time response)
  ☐ Search (citations, sources)
  ☐ Tools (command, file read, search)
  ☐ Agents (plan, execute, report)
  ☐ Settings (account, preferences)
```

---

## Week 11: Performance & Deployment

### Both Tracks
```
Goal: Production-ready infrastructure

Backend:
  Mon: Add Sentry error tracking
  Tue: Request logging (duration, status, path)
  Wed: Rate limiting (basic: 100 req/min per user)
  Thu: Caching (response cache for common queries)
  Fri: Performance profiling (find + fix slow spots)

Android:
  Mon: Baseline profile generation
  Tue: Compose performance optimization (profiling)
  Wed: Network cache configuration
  Thu: Error handling + retry for all API calls
  Fri: ProGuard/R8 optimization pass

DevOps:
  Week: Provision production server
  Week: Configure nginx + SSL
  Week: Deploy stack
  Week: Verify health monitoring
```

### Checkpoint: End of Week 11

```
Can you:
- [ ] Deploy to production server?
- [ ] Health endpoint responds?
- [ ] App connects to production API?
- [ ] No ANRs or crashes in testing?
```

---

## Week 12: Beta Launch

### Both Tracks
```
Goal: Users can install and use the app

Mon: Final bug fixes + edge case handling
Tue: Google Play store listing preparation
Wed: Build signed release APK/AAB
Thu: Internal test track setup + invite testers
Fri: Launch monitoring + hotfix if needed

Android:
  - Screenshots for Play Store
  - Privacy policy
  - Crashlytics integration
  - Beta tester feedback channel (Discord)

Backend:
  - Production monitoring
  - Backup configured
  - On-call rotation (it's you)
```

### Launch Checklist

```
☐ App builds in release mode
☐ Registration works in production
☐ Login persists across app restarts
☐ Chat messages send and receive
☐ Streaming responses work
☐ Search returns citations
☐ Tools execute commands
☐ Agents run and complete
☐ Settings save preferences
☐ App doesn't crash on rotate
☐ App background/foreground works
☐ Error states show user-friendly messages
☐ Sentry error tracking active
☐ Production server stable (>24h uptime)
```

---

## Buffer & Risk Slip

If you slip:

| Week | Cut First | Cut Second | Cut Third |
|---|---|---|---|
| 4 (streaming) | Save messages to DB | Follow-ups | N/A |
| 6 (search) | Research mode | Source favicons | N/A |
| 8 (tools) | File preview syntax highlight | Tool card expand | N/A |
| 10 (agents) | Agent history | Settings polish | N/A |
| 12 (launch) | Play Store screenshots | Beta announcement | N/A |

**Hard deadline: End of week 12.** Ship what you have. Even if agents are buggy. Even if tools are slow. Even if the settings screen is ugly. Ship.

---

## Summary

```
Week  │ Deliverable                        │ Demo to Friend
──────┼────────────────────────────────────┼─────────────────────────
1     │ Dev environments ready             │ "Can't demo yet"
2     │ Auth API working                   │ "Can't demo yet"
3     │ Android: Login → Chat list         │ "I can log in"
4     │ Chat: Send → Receive AI response   │ "I can chat with AI!" ← BIG
5     │ Streaming: Real-time text          │ "It types as I watch!" ← BIG
6     │ Search: Citations + sources        │ "It shows sources!"
7     │ Tools: Command execution           │ "It can run commands!"
8     │ Tools: File operations             │ "It reads my files!"
9     │ Agents: Autonomous research        │ "It researches for me!" ← BIG
10    │ Settings + Polish                  │ "It has settings"
11    │ Performance + Deployment           │ "Production server live"
12    │ Beta Launch                        │ "DOWNLOAD FROM PLAY STORE"
```
