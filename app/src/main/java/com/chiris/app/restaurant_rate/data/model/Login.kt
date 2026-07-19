package com.chiris.app.restaurant_rate.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)
