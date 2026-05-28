from fastapi import Request, status
from fastapi.responses import JSONResponse
import structlog

logger = structlog.get_logger()

async def global_exception_handler(request: Request, exc: Exception):
    logger.error("unhandled_exception", path=request.url.path, error=str(exc))
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={"detail": "Internal Server Error"},
    )
