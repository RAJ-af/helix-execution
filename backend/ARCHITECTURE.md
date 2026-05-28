# Architecture Decisions - Helix AI Backend

## 1. Async-First Architecture
We use `FastAPI` with `SQLAlchemy`'s async engine (`asyncpg`). This is crucial for a mobile-first app that will eventually handle long-running LLM streaming requests. Async prevents blocking the event loop during I/O operations.

## 2. Structured Logging with Structlog
Production environments require machine-readable logs. We use `structlog` to output JSON in production, which can be easily ingested by log aggregators. In development, it switches to a human-friendly console renderer.

## 3. Simple Dependency Injection
We use FastAPI's built-in dependency injection for database sessions. This ensures that every request gets its own session and that sessions are properly closed even if an error occurs.

## 4. Separation of Concerns
- **Core**: Contains global configuration, logging setup, and exception handlers.
- **DB**: Handles the connection logic and provides the base class for models.
- **Models**: Pure SQLAlchemy models.
- **API**: Versioned routes (`v1`) to allow for breaking changes in the future without breaking older mobile app versions.

## 5. Security-First Docker
The Dockerfile uses a multi-stage build to keep the final image small and runs as a non-root user (`helixuser`) to minimize the attack surface in production.

## 6. Alembic for Migrations
Even for an MVP, manual database schema changes are error-prone. Alembic provides a reliable way to track and apply schema changes across different environments.

## 7. Global Error Handling
A global exception handler ensures that we never leak internal stack traces to the mobile client, returning a clean JSON error response instead.

## 8. Authentication System
- **JWT Based**: Secure stateless authentication using Access and Refresh tokens.
- **Bcrypt Hashing**: Passwords are never stored in plain text.
- **Unified Auth Service**: Centralized logic for login, signup, and token refreshing.
- **Secure Dependencies**: `get_current_user` dependency ensures that protected routes are only accessible with a valid JWT.

## 9. Chat & Conversation System
- **Conversation State**: Managed via SQLAlchemy models with relationship optimization to fetch messages.
- **Mocked Responses**: The system generates assistant responses immediately for MVP testing.
- **Pagination & Ordering**: List endpoints support skip/limit and are ordered by the latest activity.
- **Soft Deletes**: Conversations can be marked as deleted without being purged from the database.

## 10. SSE Streaming Architecture
- **Server-Sent Events (SSE)**: Efficiently stream AI responses token-by-token to the mobile client.
- **Async Event Generators**: Uses FastAPI and `sse-starlette` to handle concurrent streaming connections.
- **Lifecycle Management**: Stream cancellation is handled via client disconnect detection.
- **Persistence**: Final assistant messages are persisted to the database asynchronously after the stream completes.

## 11. Perplexity-Style Search System
- **Search Provider Abstraction**: Supports multiple search engines via a common interface (e.g., Tavily).
- **Grounded Responses**: AI responses are contextually grounded in search snippets before streaming.
- **Rich Event Stream**: Provides `search_start`, `search_sources`, and `citation` events to the client.
- **Follow-up Generation**: Suggests relevant next questions to keep the user engaged.

## 12. Claude Code-Style Tool Execution
- **Subprocess Isolation**: Executes shell commands in dedicated per-conversation workspace directories.
- **MVP Security**: Implements a strict command blocklist and path traversal protection.
- **Streaming Output**: Live stdout/stderr is streamed to the client using SSE `tool_output` events.
- **Registry Pattern**: Easily extensible tool system with base classes for future dynamic tool calling.

## 13. Sequential Agent Execution Loop
- **Template-Based Planning**: Tasks are decomposed into a checklist of sequential steps based on the user's intent.
- **Task & Step Tracking**: Real-time updates on step status (pending, running, completed) are streamed via SSE.
- **Clarification Flow**: Supports MCQ-based user input requests to resolve ambiguities before or during execution.
- **State Persistence**: Uses `Task` and `TaskStep` models to track progress and results across sessions.

## 14. Provider-Agnostic AI Architecture
- **Unified AIProvider Interface**: Standardizes streaming and batch requests across all cloud and local models.
- **Model Router**: Intelligently routes requests to the optimal provider based on task complexity (e.g., coding -> Claude, search -> Gemini).
- **Adapter Pattern**: High-level providers (Claude, Gemini, OpenRouter) are fully implemented, while others (Ollama, Nvidia NIM) have ready-to-use scaffolds.
- **Prompt Management**: Modular prompt system for consistent AI behavior regardless of the underlying model.

## 15. AI Provider Abstraction
- **Base AIProvider Interface**: Standardizes interaction patterns (stream vs batch) across all LLM providers.
- **Provider Registry**: A centralized registry manages lifecycle and configuration of various AI adapters.
- **Model Routing Engine**: Implements logic to direct queries to the most suitable model based on intent and user preference.
- **Unified Schema**: Standardized response and tool-call formats ensure the rest of the backend remains provider-agnostic.
