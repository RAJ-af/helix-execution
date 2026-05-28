from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from app.api import deps
import structlog

router = APIRouter()
logger = structlog.get_logger()

@router.get("/health")
async def health_check(db: AsyncSession = Depends(deps.get_db)):
    """
    Health check endpoint to verify API and DB connectivity.
    """
    try:
        # Check database connectivity
        await db.execute(text("SELECT 1"))
        db_status = "connected"
    except Exception as e:
        logger.error("db_health_check_failed", error=str(e))
        db_status = "disconnected"

    return {
        "status": "online",
        "database": db_status
    }
