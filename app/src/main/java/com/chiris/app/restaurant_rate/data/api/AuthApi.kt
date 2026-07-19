package com.chiris.app.restaurant_rate.data.api

import com.chiris.app.restaurant_rate.data.model.LoginRequest
import com.chiris.app.restaurant_rate.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}