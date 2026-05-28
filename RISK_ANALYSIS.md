# Helix MVP — Risk Analysis

## Risk Scoring

```
Likelihood: 1 (Rare) → 5 (Almost certain)
Impact:     1 (Negligible) → 5 (Critical)
Score:      Likelihood × Impact (Max 25, critical > 12)
```

---

## Engineering Risks

### R1: AI Streaming Pipeline Fragility

| | |
|---|---|
| **Risk** | The chat pipeline (Android → REST → FastAPI → Claude → SSE → Android) has many failure points. Any breaks in the stream result in a terrible UX. |
| **Score** | 4 × 5 = **20 (CRITICAL)** |
| **Symptoms** | Partial responses, connection drops, corrupted JSON, timeout mid-stream |
| **Mitigation** | 1. Client-side timeout with retry (5s no data → reconnect). 2. Server-side keepalive pings every 2s over SSE. 3. Client shows partial response even if disconnected with "Response incomplete" banner. 4. Store messages client-side incrementally (not just on completion). |
| **Cost of mitigation** | ~3 days |

### R2: Subprocess Tool Executor Security

| | |
|---|---|
| **Risk** | Running AI-generated commands as subprocess on the server. User can ask the AI to run `rm -rf /` or `cat /etc/shadow`. |
| **Score** | 3 × 5 = **15 (CRITICAL)** |
| **Symptoms** | Server compromise, data loss, credential exposure |
| **Mitigation** | 1. Strict command blocklist. 2. Run as low-privilege `nobody` user. 3. Chroot/jail to `/tmp/helix_workspace`. 4. CPU/memory ulimits. 5. No network access. 6. Log ALL commands. 7. Rate limit tool executions. |
| **Cost of mitigation** | ~3 days |

### R3: AI Provider API Outage / Rate Limit

| | |
|---|---|
| **Risk** | Claude API goes down, rate-limits your key, or becomes too slow during beta testing. |
| **Score** | 3 × 4 = **12 (HIGH)** |
| **Symptoms** | All AI features broken. Users can't search, chat, or use tools. |
| **Mitigation** | 1. Add a fallback provider (GPT-4o-mini). 2. Client-side offline queue. 3. Status page. 4. Exponential backoff on 429/503. 5. Queue requests and process when API recovers. |
| **Cost of mitigation** | ~2 days for GPT fallback |

### R4: SSE Connection Handling on Android

| | |
|---|---|
| **Risk** | OkHttp streaming response body handling is fragile. Backgrounding the app kills the connection. Network changes break it. Long-lived connections drain battery. |
| **Score** | 3 × 4 = **12 (HIGH)** |
| **Symptoms** | Broken responses, blank messages, app ANR, battery drain reports |
| **Mitigation** | 1. Use a dedicated SSE library (not raw OkHttp streaming). 2. Keep connection only while message is streaming. 3. Restore connection on app foreground. 4. 30s timeout on idle connections. |
| **Cost of mitigation** | ~2 days |

### R5: Android Compose Performance

| | |
|---|---|
| **Risk** | Streaming message display causes excessive recomposition. Long conversations (100+ messages) cause jank. Terminal output display is slow. |
| **Score** | 3 × 3 = **9 (MEDIUM)** |
| **Symptoms** | Janky scrolling, frame drops, battery drain |
| **Mitigation** | 1. Use `LazyColumn` with `key` for message list. 2. Debounce streaming text updates (50ms). 3. Limit visible messages to 50, virtualize the rest. 4. Profile with Compose Compiler metrics. 5. Use `derivedStateOf` for computed state. |
| **Cost of mitigation** | ~3 days |

---

## Product Risks

### P1: LLM Hallucinates Incorrect Information

| | |
|---|---|
| **Risk** | The AI makes up facts, cites non-existent sources, or generates incorrect code. Users lose trust. |
| **Score** | 4 × 4 = **16 (CRITICAL)** |
| **Symptoms** | User reports wrong answers, bad code, fabricated citations |
| **Mitigation** | 1. Always show sources for search queries. 2. Show model name per response. 3. Add "Verify sources" button. 4. Show tool stdout/stderr for tool calls. 5. In tool responses, always show what command was run. 6. Consider showing "AI-generated, verify important information" disclaimer. |
| **Cost of mitigation** | ~2 days|

### P2: Agent Runs Too Long or Loops

| | |
|---|---|
| **Risk** | An autonomous agent enters an infinite loop, consumes excessive tokens, or never produces a useful result. |
| **Score** | 3 × 4 = **12 (HIGH)** |
| **Symptoms** | High token consumption, user frustration, never-completing tasks |
| **Mitigation** | 1. Hard limit of 10 steps per agent run. 2. Max 3 retries per step. 3. Global 5-minute timeout. 4. Max 50K tokens per agent run. 5. "Cancel" button always visible. 6. LLM prompt includes "If you cannot complete this task, explain why and stop." |
| **Cost of mitigation** | ~1 day |

### P3: Tool Execution is Too Slow

| | |
|---|---|
| **Risk** | Running commands via subprocess on the server takes seconds. The user is waiting. Feels broken compared to ChatGPT's instant text. |
| **Score** | 4 × 3 = **12 (HIGH)** |
| **Symptoms** | Users don't use tools, think app is slow, churn |
| **Mitigation** | 1. Show tool output in real-time, character by character. 2. Show a spinner/timer for tool calls. 3. Set LLM expectation: "Running: command..." before output starts. 4. Cache common results (e.g., `ls /workspace`). 5. Parallel tool execution where possible. |
| **Cost of mitigation** | ~2 days |

### P4: Mobile Keyboard UX is Terrible

| | |
|---|---|
| **Risk** | Typing complex queries, code, or multi-line input on mobile keyboard is painful. Users abandon the app. |
| **Score** | 5 × 2 = **10 (HIGH)** |
| **Symptoms** | Low messages-per-session, high bounce rate |
| **Mitigation** | 1. Voice input (Android speech-to-text). 2. Clipboard paste button. 3. "Share to Helix" intent. 4. Example prompts as tap targets. 5. Web search deep links. |
| **Cost of mitigation** | ~2 days |

### P5: No User Retention Loop

| | |
|---|---|
| **Risk** | Users try the app once, are impressed, but never return. No daily habit formed. |
| **Score** | 4 × 3 = **12 (HIGH)** |
| **Symptoms** | Low DAU/MAU ratio, high D7 drop-off |
| **Mitigation** | 1. Push notifications for agent completion. 2. "Ask again tomorrow" feature. 3. Saved agent workflows. 4. Share results. 5. Conversation continuation. For MVP, focus on making the first use so impressive they tell a friend. |
| **Cost of mitigation** | ~1 week (mostly post-MVP) |

---

## Technical Debt Risks

### T1: No Tests for MVP

| | |
|---|---|
| **Risk** | Skipping tests to move faster. Every refactor breaks things. Regressions go to production. |
| **Score** | 4 × 3 = **12 (HIGH)** |
| **Mitigation** | 1. Test the AI pipeline (send message → get response) end-to-end. 2. Test the streaming parser. 3. Everything else is manual testing. 4. Add automated tests post-MVP. |
| **Cost of mitigation** | ~2 days for core tests |

### T2: No Monitoring

| | |
|---|---|
| **Risk** | No Prometheus, no Grafana, no structured logging. When things break, you don't know why. |
| **Score** | 4 × 3 = **12 (HIGH)** |
| **Mitigation** | 1. Sentry for error tracking (free tier). 2. Basic request logging to stdout. 3. Health endpoint. 4. That's enough for MVP. |
| **Cost of mitigation** | ~1 day |

### T3: Monolithic Backend

| | |
|---|---|
| **Risk** | Everything in one FastAPI app. As you add features, the codebase becomes spaghetti. |
| **Score** | 2 × 3 = **6 (LOW)** |
| **Mitigation** | Use simple folder separation (routes/, services/, models/). Don't over-abstract. You can extract services later. |
| **Cost of mitigation** | ~0 (just organize folders) |

---

## Risk Matrix Summary

| ID | Risk | Score | Priority |
|---|---|---|---|
| R1 | AI streaming pipeline fragility | 20 | P0 |
| P1 | LLM hallucination / bad info | 16 | P0 |
| R2 | Subprocess tool security | 15 | P0 |
| R3 | AI provider outage | 12 | P1 |
| R4 | SSE on Android | 12 | P1 |
| P2 | Agent loops | 12 | P1 |
| P3 | Tool execution too slow | 12 | P1 |
| P5 | No retention loop | 12 | P1 |
| T1 | No tests | 12 | P1 |
| T2 | No monitoring | 12 | P1 |
| R5 | Compose performance | 9 | P2 |
| P4 | Mobile keyboard UX | 10 | P2 |
| T3 | Monolithic backend | 6 | P3 |

---

## Risk Response Plan

### P0 (Ship-blocking): Must fix before launch
1. Streaming pipeline must be solid (R1) — test with poor network conditions
2. Source display to counter hallucination (P1) — non-negotiable for search credibility
3. Subprocess safety (R2) — strip capabilities, chroot, ulimit

### P1 (High priority): Fix before public launch
4. Fallback AI provider (R3) — min 1 alternative provider configured
5. SSE client library (R4) — don't build your own
6. Agent hard limits (P2) — step count, timeout, cancel
7. Real-time tool output (P3) — streaming, not batch

### P2 (Important): Fix during beta
8. Compose performance tuning (R5)
9. Voice input / keyboard UX (P4)
10. Basic monitoring (T2)
