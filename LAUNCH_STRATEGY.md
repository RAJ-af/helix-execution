# Helix MVP — Launch Strategy

## The Goal

Ship a working Android app that demonstrates **AI search, tool execution, and autonomous agents** — to a small set of beta users who will give you feedback, not expect perfection.

---

## Pre-Launch: Week 10-11

### What To Test

**Critical path (test EVERY deploy):**
1. Register new user → complete flow
2. Login → existing user → message history loads
3. Send message → AI responds (streaming)
4. Ask search question → sources appear
5. Ask tool question → tool runs, output shows
6. Create agent → agent executes → completes
7. Settings → change preference → persists
8. Logout → login again → state preserved

**Test on real devices (min 3):**
- Pixel 7 / Samsung S23 (primary)
- Older device (OnePlus 6T or similar)
- Budget device (Moto G series)

### What NOT to Test

- Edge cases in agent failure modes
- Load testing (>10 concurrent users)
- Security penetration testing
- Internationalization
- Accessibility (WCAG audit)

### Pre-launch Checklist

```
☐ Production server running (single VPS, $40/mo)
☐ PostgreSQL running with backup configured
☐ Redis running (if used)
☐ SSL certificate active (Let's Encrypt)
☐ Sentry error tracking configured
☐ Health endpoint responding
☐ All environment variables set
☐ nginx reverse proxy configured
☐ Firewall configured (ufw: 22, 80, 443 only)
☐ Release APK builds successfully
☐ App launches on 3+ test devices
☐ Critical path tested end-to-end
☐ Crashlytics configured
☐ Google Play Console account active
☐ Privacy policy written
☐ Terms of service written
☐ Beta tester group created (Discord or email list)
```

---

## Launch Day: Week 12

### Rollout Plan

```
Phase 1: Internal (Day 1-2)
  - 5 testers (friends, co-founders)
  - Monitor Sentry + Crashlytics closely
  - Fix critical bugs within hours
  - Goal: Ensure basic flow works for real users

Phase 2: Closed Beta (Day 3-7)
  - 50 testers (invite-only)
  - Google Play internal test track
  - Collect feedback via Discord/in-app
  - Fix important bugs within 24h
  - Goal: Validate product-market fit signals

Phase 3: Open Beta (Week 3+)
  - Open to anyone with link
  - Google Play open test track
  - Start collecting ratings
  - Goal: Growth with controlled quality
```

### What to Monitor

```
Daily checks (first 2 weeks):
  ☐ New user registrations
  ☐ Messages sent (total)
  ☐ Searches performed
  ☐ Tools executed
  ☐ Agent tasks started/completed
  ☐ Crash-free rate (target: >99.5%)
  ☐ P95 response time
  ☐ Server CPU/memory
  ☐ AI API token usage
  ☐ AI API error rate
  ☐ Top errors in Sentry

If crash-free rate < 99% → halt rollout
If P95 response time > 10s → investigate immediately
If AI API error rate > 5% → switch to fallback or degrade gracefully
```

### Communication Plan

```
Launch day:
  - Post in beta tester Discord: "It's live! Here's what to try first."
  - Include 3 suggested prompts:
    1. "Search: What's new in Android 16?"
    2. "Find all TODO comments in this project" (attach a zip)
    3. "Research best practices for Kotlin coroutines"

Day 2:
  - "Thanks for testing! Any issues? Reply in this thread."
  - Monitor feedback + triage bugs
  - Quick patch for critical issues

Day 7:
  - "Week 1 recap: X users, Y messages, Z agents. Top feedback: ..."
  - Share what you're fixing next
  - Ask for Google Play rating
```

---

## Post-Launch: Weeks 13-16

### Immediate Priorities (by priority)

1. **Fix critical bugs** — Crashes, data loss, auth failures
2. **Address top 3 user complaints** — What breaks the experience
3. **Performance optimization** — Slow streaming, janky UI
4. **Missing features** — What users expected but wasn't there
5. **Add tests** — Start with the AI pipeline

### What NOT to do post-launch

- Don't rewrite the architecture
- Don't add Docker sandbox yet
- Don't add local models
- Don't build the web app
- Don't scale infrastructure
- Don't add analytics beyond Crashlytics

### Measure Before Building

Before adding ANY feature, ask:
- Did 3+ beta users ask for it?
- Does it directly improve retention?
- Can we fake it with 50 lines of code?

If no to all → don't build it.

---

## What "Good Enough" Looks Like

The MVP is ready to launch when:

```
Home screen:   Search bar + recent conversations
Chat:          Send message → streaming AI response
Search:        Ask question → cited answer with sources
Tools:         Ask to run command → terminal output in chat
Agents:        Ask to research → steps shown → final report
Settings:      Account info + dark theme + logout
Performance:   Cold start < 3s, streaming starts < 2s
Stability:     No crashes on 3 test devices, 24h uptime
```

---

## Biggest Launch Risks

| Risk | Mitigation |
|---|---|
| Server goes down at launch | Single VPS with auto-restart. Fix in 30 min or wake up. |
| AI API bill spikes | Hard cap at $500/day. Notify on $200/day threshold. |
| Android app crashes on launch | Test on 3 devices pre-launch. 24h internal test. |
| Users don't understand the app | 3 suggested prompts on first launch. In-app tooltips. |
| Streaming doesn't work on slow networks | 2-second timeout on connection. Show cached response if offline. |

---

## Launch Budget (Monthly)

| Item | Cost |
|---|---|
| Production server (VPS) | $40-80 |
| Domain + SSL | $2 |
| AI API (Claude) | $500-2000 |
| Google Play account | $25 (one-time) |
| Sentry (free tier) | $0 |
| **Total** | **~$567-2107/mo** |

---

## The One-Sentence Launch Strategy

**Ship the simplest thing that demonstrates the core idea — an AI that can search, execute code, and run autonomously — to 50 beta users, fix what breaks, then decide what to build next based on what they actually use.**
