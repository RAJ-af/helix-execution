from typing import List
from pydantic import AnyHttpUrl
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    PROJECT_NAME: str = "Helix AI"
    API_V1_STR: str = "/api/v1"

    # POSTGRES_SERVER: str = "localhost"
    # POSTGRES_USER: str = "postgres"
    # POSTGRES_PASSWORD: str = "postgres"
    # POSTGRES_DB: str = "helix"
    DATABASE_URL: str = "postgresql+asyncpg://postgres:postgres@db:5432/helix"

    BACKEND_CORS_ORIGINS: List[AnyHttpUrl] = []

    LOG_LEVEL: str = "INFO"
    ENVIRONMENT: str = "development"

    model_config = SettingsConfigDict(
        env_file=".env",
        case_sensitive=True,
        extra="ignore"
    )

settings = Settings()
