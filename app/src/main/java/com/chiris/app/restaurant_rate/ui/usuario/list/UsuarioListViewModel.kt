package com.chiris.app.restaurant_rate.ui.usuario.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.UsuarioApi
import com.chiris.app.restaurant_rate.data.model.Usuario
import com.chiris.app.restaurant_rate.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UsuarioListViewModel : ViewModel() {

    var usuarios by mutableStateOf<List<Usuario>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Se activa cuando el backend responde 403: el usuario autenticado no es ADMIN.
    var forbidden by mutableStateOf(false)
        private set

    private val service = ApiClient.retrofit.create(UsuarioApi::class.java)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun loadUsuarios() {
        viewModelScope.launch {
            try {
                isLoading = true
                usuarios = service.getAllUsuarios()
                forbidden = false
                error = null
            } catch (e: HttpException) {
                if (e.code() == 403) {
                    forbidden = true
                    error = null
                } else {
                    error = "Error cargando usuarios (${e.code()})"
                }
            } catch (e: Exception) {
                error = "Error de conexión al cargar usuarios"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUsuario(id: Long) {
        viewModelScope.launch {
            try {
                val response = service.deleteUsuario(id)
                if (response.isSuccessful) {
                    loadUsuarios()
                    _snackbarMessage.value = "Usuario eliminado correctamente"
                } else if (response.code() == 409) {
                    // El backend devuelve 409 si el usuario tiene reseñas asociadas.
                    _snackbarMessage.value = "No se puede eliminar: el usuario tiene reseñas asociadas"
                } else {
                    _snackbarMessage.value = "Error al eliminar usuario (${response.code()})"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error de conexión al eliminar"
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
