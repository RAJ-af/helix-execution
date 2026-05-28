package com.helix.app.core.data.repository

import com.helix.app.core.common.Resource
import com.helix.app.core.data.local.prefs.TokenManager
import com.helix.app.core.data.remote.AuthService
import com.helix.app.core.data.remote.dto.*
import com.helix.app.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Flow<Resource<TokenResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.login(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveTokens(it.access_token, it.refresh_token)
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun signup(request: SignupRequest): Flow<Resource<UserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.signup(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun logout() {
        tokenManager.deleteTokens()
    }
}
