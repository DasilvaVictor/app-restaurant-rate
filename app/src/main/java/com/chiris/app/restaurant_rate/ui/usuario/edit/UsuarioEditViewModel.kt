package com.chiris.app.restaurant_rate.ui.usuario.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.UsuarioApi
import com.chiris.app.restaurant_rate.data.model.Rol
import com.chiris.app.restaurant_rate.data.model.UsuarioUpdateRequest
import com.chiris.app.restaurant_rate.data.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UsuarioEditViewModel : ViewModel() {

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    // Nueva contraseña (opcional): solo se envía si el admin escribe algo.
    var password by mutableStateOf("")
    var rol by mutableStateOf(Rol.USER)

    var isLoading by mutableStateOf(true)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var updateSuccess by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)

    private val service = ApiClient.retrofit.create(UsuarioApi::class.java)

    fun load(id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                val usuario = service.getUsuario(id)
                nombre = usuario.nombre
                email = usuario.email
                rol = usuario.rol
            } catch (e: HttpException) {
                error = if (e.code() == 403)
                    "No tienes permisos para editar usuarios"
                else
                    "Error al cargar el usuario (${e.code()})"
            } catch (e: Exception) {
                error = "Error de conexión al cargar el usuario"
            } finally {
                isLoading = false
            }
        }
    }

    fun save(id: Long) {
        if (nombre.isBlank() || email.isBlank()) {
            error = "El nombre y el email son obligatorios"
            return
        }
        viewModelScope.launch {
            try {
                isSaving = true
                // PATCH parcial: la contraseña solo viaja si se escribió una nueva.
                val updates = UsuarioUpdateRequest(
                    nombre = nombre.trim(),
                    email = email.trim(),
                    password = password.ifBlank { null },
                    rol = rol
                )
                val response = service.patchUsuario(id, updates)
                when {
                    response.isSuccessful -> {
                        updateSuccess = true
                        error = null
                    }
                    response.code() == 409 -> error = "El email ya está registrado"
                    response.code() == 403 -> error = "No tienes permisos para editar usuarios"
                    else -> error = "No se pudo guardar (${response.code()})"
                }
            } catch (e: HttpException) {
                error = "No se pudo guardar (${e.code()})"
            } catch (e: Exception) {
                error = "Error de conexión al guardar"
            } finally {
                isSaving = false
            }
        }
    }
}
