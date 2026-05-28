# Helix AI Backend

This is the backend foundation for the Helix AI app.

## Tech Stack
- **FastAPI**: Modern, fast (high-performance) web framework.
- **PostgreSQL**: Robust relational database.
- **SQLAlchemy (Async)**: Async SQL toolkit and ORM.
- **Alembic**: Database migrations.
- **Docker Compose**: Container orchestration for development.
- **structlog**: Structured JSON logging.

## Project Structure
```
backend/
├── alembic/            # Database migrations
├── app/
│   ├── api/            # API endpoints (v1)
│   ├── core/           # Configuration and logging
│   ├── db/             # Database connection and base class
│   ├── models/         # SQLAlchemy models
│   ├── schemas/        # Pydantic schemas
│   └── main.py         # App entry point
├── Dockerfile          # Production-ready Docker setup
├── docker-compose.yml  # Local development setup
└── requirements.txt    # Python dependencies
```

## Getting Started

### Local Development (with Docker)
1. **Clone the repository**
2. **Setup environment variables**:
   ```bash
   cp .env.example .env
   ```
3. **Start the services**:
   ```bash
   docker-compose up --build
   ```
4. **Run migrations**:
   ```bash
   docker-compose exec api alembic upgrade head
   ```
5. **Access the API**:
   - API: http://localhost:8000
   - Docs: http://localhost:8000/docs
   - Health check: http://localhost:8000/api/v1/health

### Local Development (without Docker)
1. **Create a virtual environment**:
   ```bash
   python3 -m venv venv
   source venv/bin/activate
   ```
2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```
3. **Run the application**:
   ```bash
   uvicorn app.main:app --reload
   ```

## Key Features
- **Async-First**: Fully asynchronous database and API operations.
- **Non-Root User**: Docker container runs as `helixuser` for security.
- **Structured Logging**: JSON logs in production, human-readable in development.
- **Production Quality**: Separation of concerns, environment-based configuration, and robust error handling.
