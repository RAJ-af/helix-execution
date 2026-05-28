# Helix MVP — Feature Implementation Order

## The Golden Rule

**Build in the order that unblocks the most downstream work.**

```
                 ┌──────────┐
                 │  Auth    │ ← No one can use the app without this
                 └────┬─────┘
                      │
                 ┌────▼─────┐
                 │  Chat    │ ← The container for ALL features
                 └────┬─────┘
                      │
         ┌────────────┼────────────┐
         │            │            │
    ┌────▼───┐  ┌────▼───┐  ┌────▼──────┐
    │Stream  │  │ Search │  │  Tools     │
    │ (SSE)  │  │        │  │           │
    └────┬───┘  └────┬───┘  └────┬──────┘
         │           │            │
         └───────────┼────────────┘
                     │
               ┌─────▼──────┐
               │   Agents   │ ← Depends on everything above
               └─────┬──────┘
                     │
               ┌─────▼──────┐
               │  History   │ ← Nice to have, low dependency
               │ + Settings │
               └────────────┘
```

---

## Implementation Order Table

```
Order | Feature          | Depends On     | Blocks         | Difficulty  | Estimate
──────┼──────────────────┼────────────────┼────────────────┼─────────────┼─────────
1     │ Backend scaffold │ Nothing        │ Everything     │ Easy        │ 2 days
2     │ Database schema  │ Backend        │ All API work   │ Easy        │ 1 day
3     │ Auth API         │ Database       │ All auth'd req │ Medium      │ 3 days
4     │ Auth UI          │ Auth API       │ Chat screen    │ Medium      │ 3 days
5     │ Chat API         │ Auth, Database │ Streaming      │ Medium      │ 3 days
6     │ Chat UI          │ Auth UI        │ All UI screens │ Medium      │ 4 days
7     │ SSE streaming    │ Chat API       │ Search, Tools  │ Hard        │ 4 days
8     │ Streaming UI     │ Chat UI, SSE   │ Answer display │ Medium      │ 2 days
9     │ Search pipeline  │ SSE            │ Search UI      │ Hard        │ 4 days
10    │ Search UI + cites│ Streaming UI   │ Agent search   │ Medium      │ 3 days
11    │ Tool framework   │ SSE            │ Tool executor  │ Hard        │ 3 days
12    │ Tool executor    │ Tool framework │ Tool UI        │ Hard        │ 3 days
13    │ Tool UI          │ Streaming UI   │ Agent tools    │ Medium      │ 3 days
14    │ Agent system     │ Search, Tools  │ Agent UI       │ Very Hard   │ 5 days
15    │ Agent UI         │ Agent system   │ —             │ Hard        │ 4 days
16    │ History + Room   │ Chat           │ Settings       │ Medium      │ 3 days
17    │ Settings screen  │ Auth UI        │ —             │ Easy        │ 2 days
18    │ Performance pass │ Everything     │ Launch         │ Medium      │ 3 days
19    │ Deployment       │ Backend        │ Beta            │ Medium      │ 2 days
20    │ Beta launch      │ All above      │ —             │ Easy        │ 1 day
```

---

## Dependency Graph (Visual)

```
Week 1            Week 2           Week 3           Week 4
┌─────────┐      ┌─────────┐     ┌─────────┐
│ Backend │──────│ Auth API │     │ Chat API│
│Scaffold │      │         │     │         │
└─────────┘      └─────────┘     └────┬────┘
                                      │
┌─────────┐      ┌─────────┐         │
│ Database│──────│ Auth UI │         │
│ Schema  │      │         │         │
└─────────┘      └────┬────┘         │
                      │              │
                      │    ┌─────────▼──────┐
                      │    │ Chat UI        │
                      │    │ + MessageList  │
                      │    └────────┬───────┘
                      │             │
                      │    ┌────────▼───────┐
                      └────┤ SSE Client     │
                           │ (Android)      │
                           └────────┬───────┘
                                    │
Week 5            Week 6           Week 7           Week 8
         ┌────────┴────────┐
         │                 │
  ┌──────▼──────┐  ┌──────▼───────┐
  │ SSE Backend │  │ Search API   │
  │ (Python)    │  │ Pipeline     │
  └──────┬──────┘  └──────┬───────┘
         │                │
  ┌──────▼──────┐  ┌──────▼───────┐
  │ Search UI   │  │ Streaming    │
  │ + Sources   │  │ Answer       │
  └──────┬──────┘  └──────┬───────┘
         │                │
         └────────┬───────┘
                  │
         ┌────────▼───────┐
         │ Tool Framework │
         └────────┬───────┘
                  │
         ┌────────▼───────┐
         │ Tool Executor  │
         └────────┬───────┘
                  │
         ┌────────▼───────┐
         │ Tool UI        │
         └────────┬───────┘
                  │
Week 9-10        │
         ┌────────▼───────┐
         │ Agent System   │
         └────────┬───────┘
                  │
         ┌────────▼───────┐
         │ Agent UI       │
         └────────┬───────┘
                  │
         ┌────────▼───────┐
         │ Performance +  │
         │ Polish         │
         └────────┬───────┘
                  │
         ┌────────▼───────┐
         │  BETA LAUNCH   │
         └────────────────┘
```

---

## What Should Be Built First

### Absolute foundation (Day 1-3)
1. FastAPI project with health endpoint
2. Docker Compose with Postgres + Redis
3. Android project with navigation scaffold
4. Empty Compose screens for all destinations

**Why**: Unblocks ALL work. Both developers can start immediately.

### First user-visible feature (Week 2-3)
5. Auth (backend + Android)
6. Empty chat screen with input bar

**Why**: This is the first moment you can test the full pipeline.

### First "wow" moment (Week 4-5)
7. Chat → AI response with streaming
8. Even without search, the AI conversation is compelling

**Why**: This proves the core value proposition. Everything else is additive.

---

## What Should NOT Be Built Yet

### Never build these in MVP:
- **Multi-model routing** — Claude Sonnet handles everything
- **Ollama/local models** — High complexity, narrow appeal
- **Multi-agent orchestration** — One agent is hard enough
- **Workspace system** — Flat conversation list works fine
- **Plugin system** — You don't have partners yet
- **Admin dashboard** — Manually query the database
- **Rate limiting** — Add when you have traffic
- **Caching layer** — Add when you have latency problems

### Delay to post-MVP:
- **Push notifications** — Polling works for beta
- **Vector embeddings** — Store as JSON, don't need semantic search
- **Full test suite** — Test the critical path only
- **CI/CD pipeline** — Manual deploy to a single server
- **Analytics** — Firebase Crashlytics only
- **File diff viewer** — Show raw output instead
- **Research mode** — Single search is good enough

---

## Fastest Path to Launch

```
Week 1-2: Auth + Chat (without streaming)
  → Value: User can send a message

Week 3-4: Add streaming
  → Value: User sees AI think in real-time

Week 5-6: Add search + citations
  → Value: Perplexity-like experience

Week 7-8: Add tools
  → Value: Claude Code-like experience

Week 9-10: Add basic agents
  → Value: Autonomous task execution

Week 11-12: Polish + Launch
  → Value: Production-ready MVP
```

Each week adds a new capability. The app is functional (though limited) from week 2. By week 6, it's compelling. By week 10, it's differentiated.
