package com.helix.app.core.domain.repository

import com.helix.app.core.common.Resource
import com.helix.app.core.data.remote.dto.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Flow<Resource<TokenResponse>>
    suspend fun signup(request: SignupRequest): Flow<Resource<UserResponse>>
    suspend fun logout()
}
