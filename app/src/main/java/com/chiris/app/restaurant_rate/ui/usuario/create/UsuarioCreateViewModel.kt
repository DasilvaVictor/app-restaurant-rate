package com.chiris.app.restaurant_rate.ui.usuario.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.UsuarioApi
import com.chiris.app.restaurant_rate.data.model.Rol
import com.chiris.app.restaurant_rate.data.model.UsuarioRequest
import com.chiris.app.restaurant_rate.data.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UsuarioCreateViewModel : ViewModel() {

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var rol by mutableStateOf(Rol.USER)

    var isSaving by mutableStateOf(false)
        private set

    var createSuccess by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)

    private val service = ApiClient.retrofit.create(UsuarioApi::class.java)

    fun create() {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            error = "Nombre, email y contraseña son obligatorios"
            return
        }
        viewModelScope.launch {
            try {
                isSaving = true
                val response = service.createUsuario(
                    UsuarioRequest(
                        nombre = nombre.trim(),
                        email = email.trim(),
                        password = password,
                        rol = rol
                    )
                )
                when {
                    response.isSuccessful -> {
                        createSuccess = true
                        error = null
                    }
                    response.code() == 409 -> error = "El email ya está registrado"
                    response.code() == 403 -> error = "No tienes permisos para crear usuarios"
                    else -> error = "No se pudo crear el usuario (${response.code()})"
                }
            } catch (e: HttpException) {
                error = "No se pudo crear el usuario (${e.code()})"
            } catch (e: Exception) {
                error = "Error de conexión al crear usuario"
            } finally {
                isSaving = false
            }
        }
    }
}
