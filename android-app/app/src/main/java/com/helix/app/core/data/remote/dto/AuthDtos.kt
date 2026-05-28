package com.helix.app.core.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val email: String,
    val password: String,
    val full_name: String? = null
)

data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String
)

data class RefreshRequest(
    val refresh_token: String
)

data class UserResponse(
    val id: Int,
    val email: String,
    val full_name: String?,
    val is_active: Boolean,
    val created_at: String
)
