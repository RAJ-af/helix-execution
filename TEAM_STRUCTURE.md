# Helix MVP — Team Structure & Responsibility

## The Reality: 1-3 Person Team

This plan assumes the most likely startup scenario: **a very small team building the MVP.**

Three viable team configurations:

### Configuration A: Solo Founder (Full Stack + AI)

```
1 Person
├── Android (compromised — uses WebView or simpler UI)
├── Backend (FastAPI — strong)
├── AI/LLM Integration
├── DevOps
└── Product/Design

Strategy:
- Offload Android to a simpler WebView-based PWA for MVP
- Build native Android Post-MVP
- Focus 100% on the backend AI pipeline
- Use Supabase for auth + DB (zero DevOps)
- Timeline: 6-8 weeks
```

### Configuration B: Two-Person Team (Recommended)

```
Person 1: Backend + AI + DevOps
├── FastAPI backend
├── Claude API integration
├── Search pipeline
├── Tool execution
├── Server deployment
└── CI/CD

Person 2: Android + UI/UX
├── Jetpack Compose UI
├── Navigation + state
├── SSE client integration
├── Design system
├── Play Store submission
└── Light backend API work

Shared:
├── API contract design
├── Testing
├── Product decisions
└── Bug triage

Strategy:
- Backend-first. Person 1 starts 2 weeks before Person 2.
- Person 2 builds UI against mock API responses initially.
- Person 1 handles ALL infrastructure (no DevOps specialist).
- Timeline: 10-12 weeks
```

### Configuration C: Three-Person Team (Ideal)

```
Person 1: Backend Lead
├── FastAPI architecture
├── Database
├── AI integration
├── Search pipeline
├── Performance
└── Production readiness

Person 2: Android Lead
├── Compose architecture
├── All screens
├── SSE + networking
├── Navigation
├── Google Play
└── Performance

Person 3: AI/Full Stack
├── Agent system
├── Tool execution
├── Search ranking
├── Prompt engineering
├── Chat streaming pipeline
└── Help on Android or backend as needed

Strategy:
- Person 3 starts on the AI pipeline (hardest part)
- Person 1 and 2 work in parallel on their domains
- Weekly sync on API contract
- Timeline: 8-10 weeks
```

---

## Role Definitions

### Backend Developer

```
Required skills:
- Python 3.11+ (FastAPI experience critical)
- PostgreSQL / SQL
- Docker basics
- REST API design
- Async programming (asyncio)

Nice to have:
- Celery experience
- SSE / WebSocket
- LLM API integration
- Redis

Day-to-day:
- Build API endpoints
- Integrate Claude API
- Build search pipeline
- Implement tool executor
- Database schema + queries
- Deploy to server
```

### Android Developer

```
Required skills:
- Kotlin (Jetpack Compose essential)
- Hilt / DI
- OkHttp / Retrofit
- Room database
- Coroutines / Flow
- Navigation Compose

Nice to have:
- SSE client implementation
- ProGuard / R8
- Google Play Console
- Firebase

Day-to-day:
- Build all screens
- Implement navigation
- Connect to API
- Streaming response display
- Local caching
- Play Store submission
```

### AI / Full Stack Developer

```
Required skills:
- Prompt engineering
- Python (FastAPI or similar)
- LLM API integration (Claude, OpenAI)
- Tool-use / function-calling patterns
- Agent design patterns

Nice to have:
- Search relevance / ranking
- Docker
- TypeScript / React (for eventual web app)

Day-to-day:
- Design prompt templates
- Implement tool-use loop
- Build agent execution flow
- Optimize search -> answer pipeline
- Handle edge cases in AI responses
```

---

## Task Ownership Matrix

```
Task                    | Backend | Android | AI/FS  | Priority
────────────────────────┼─────────┼─────────┼────────┼─────────
Project setup           │ B       │ A       │        │ Week 1
Database schema         │ B       │         │        │ Week 1
Auth API                │ B       │         │        │ Week 1-2
Auth UI                 │         │ A       │        │ Week 2-3
Chat API                │ B       │         │ A      │ Week 2-3
Chat UI                 │         │ A       │        │ Week 3-4
Streaming (SSE)         │ B       │ A       │        │ Week 3-4
Search API              │ B       │         │ A      │ Week 4-5
Search UI               │         │ A       │        │ Week 5-6
Citations UI            │         │ A       │        │ Week 5-6
Tool framework          │         │         │ A      │ Week 5-7
Tool executor           │ B       │         │ A      │ Week 6-7
Tool UI                 │         │ A       │        │ Week 7-8
Agent system            │         │         │ A      │ Week 7-9
Agent UI                │         │ A       │        │ Week 8-9
Settings UI             │         │ A       │        │ Week 8
History / Room          │ B       │ A       │        │ Week 4-5
Prompt engineering      │         │         │ A      │ Ongoing
Performance             │ B       │ A       │        │ Week 9-10
Deployment              │ B       │ A       │        │ Week 9-10
Beta launch             │ Both    │ Both    │ Both   │ Week 10-12

Key: A = Primary owner, B = Secondary/support
```

---

## Communication Cadence

### Daily (15 min)
- What did you do yesterday?
- What are you doing today?
- Any blockers?

### Weekly (30 min)
- Demo what's working
- Review API contract changes
- Prioritize next week's tasks
- Re-evaluate scope

### Bi-weekly (60 min)
- Full product review
- User testing results (if any)
- Adjust roadmap
- Celebrate wins

---

## What Each Role Should NOT Do

### Backend should NOT:
- Design UI components
- Write Android code
- Set up Google Play
- Manage the LLM prompt library (that's AI role)

### Android should NOT:
- Design API contracts (but must review them)
- Write Python code
- Manage servers
- Do prompt engineering

### AI/FS should NOT:
- Build screens
- Write migrations
- Set up CI/CD
- Handle auth tokens

Exception: In a 2-person team, everyone does everything. These boundaries blur. The above is for the ideal 3-person configuration.
