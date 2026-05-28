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
