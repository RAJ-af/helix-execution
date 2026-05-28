from datetime import timedelta
from typing import Any
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from jose import jwt, JWTError

from app.api import deps
from app.core import security
from app.models.user import User
from app.schemas.auth import Token, LoginRequest, RefreshRequest, TokenPayload
from app.schemas.user import User as UserSchema, UserCreate
import structlog

router = APIRouter()
logger = structlog.get_logger()

@router.post("/signup", response_model=UserSchema)
async def signup(
    *,
    db: AsyncSession = Depends(deps.get_db),
    user_in: UserCreate
) -> Any:
    """
    Create new user.
    """
    result = await db.execute(select(User).where(User.email == user_in.email))
    user = result.scalar_one_or_none()
    if user:
        raise HTTPException(
            status_code=400,
            detail="A user with this email already exists in the system.",
        )

    db_user = User(
        email=user_in.email,
        hashed_password=security.get_password_hash(user_in.password),
        full_name=user_in.full_name,
    )
    db.add(db_user)
    await db.commit()
    await db.refresh(db_user)
    return db_user

@router.post("/login", response_model=Token)
async def login(
    db: AsyncSession = Depends(deps.get_db),
    login_data: LoginRequest = None
) -> Any:
    """
    Login user.
    """
    result = await db.execute(select(User).where(User.email == login_data.email))
    user = result.scalar_one_or_none()

    if not user or not security.verify_password(login_data.password, user.hashed_password):
        raise HTTPException(status_code=400, detail="Incorrect email or password")
    elif not user.is_active:
        raise HTTPException(status_code=400, detail="Inactive user")

    return {
        "access_token": security.create_access_token(user.id),
        "refresh_token": security.create_refresh_token(user.id),
        "token_type": "bearer",
    }

@router.post("/refresh", response_model=Token)
async def refresh_token(
    refresh_data: RefreshRequest
) -> Any:
    """
    Refresh access token.
    """
    try:
        payload = jwt.decode(
            refresh_data.refresh_token, security.SECRET_KEY, algorithms=[security.ALGORITHM]
        )
        token_data = TokenPayload(**payload)
        if token_data.type != "refresh":
             raise HTTPException(status_code=401, detail="Invalid refresh token")
    except (JWTError):
        raise HTTPException(status_code=401, detail="Invalid refresh token")

    return {
        "access_token": security.create_access_token(token_data.sub),
        "refresh_token": refresh_data.refresh_token, # Reuse refresh token
        "token_type": "bearer",
    }

@router.get("/me", response_model=UserSchema)
async def get_me(
    current_user: User = Depends(deps.get_current_user),
) -> Any:
    """
    Get current user.
    """
    return current_user

@router.post("/logout")
async def logout():
    """
    Logout user (Client should just delete the token).
    """
    return {"message": "Successfully logged out"}
