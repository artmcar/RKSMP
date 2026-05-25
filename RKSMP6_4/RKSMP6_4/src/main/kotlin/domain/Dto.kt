package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val expiresAt: Long)

@Serializable
data class ErrorResponse(val error: String)
