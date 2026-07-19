package com.chiris.app.restaurant_rate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.model.LoginRequest
import com.chiris.app.restaurant_rate.data.api.AuthApi
import com.chiris.app.restaurant_rate.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.*
import com.chiris.app.restaurant_rate.data.network.ApiClient
import com.chiris.app.restaurant_rate.data.network.TokenManager

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var token by mutableStateOf<String?>(null)
    var error by mutableStateOf<String?>(null)

    var loginSuccess = mutableStateOf(false)

    private val service = ApiClient.retrofit.create(AuthApi::class.java)

    fun resetLoginState() {
        loginSuccess.value = false
    }
    fun login() {
        viewModelScope.launch {
            try {
                val response = service.login(
                    LoginRequest(email, password)
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        token = body.token
                        TokenManager.token = body.token // 👈 AQUÍ sí
                        loginSuccess.value = true
                        error = null
                    } else {
                        error = "Respuesta vacía del servidor"
                    }

                } else {
                    error = "Credenciales incorrectas"
                }

            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }
}