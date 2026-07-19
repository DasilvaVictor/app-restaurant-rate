package com.chiris.app.restaurant_rate.ui.restaurant.edit

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.RestaurantApi
import com.chiris.app.restaurant_rate.data.model.RestaurantUpdateRequest
import com.chiris.app.restaurant_rate.data.network.ApiClient
import kotlinx.coroutines.launch

class RestaurantEditViewModel : ViewModel() {

    var nombre by mutableStateOf("")
    var tipoComida by mutableStateOf("")
    var direccion by mutableStateOf("")
    var telefono by mutableStateOf("")

    var isLoading by mutableStateOf(true)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var updateSuccess by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)

    private val service = ApiClient.retrofit.create(RestaurantApi::class.java)

    fun load(id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                val detail = service.getRestaurantDetail(id)
                nombre = detail.nombre
                tipoComida = detail.tipoComida
                direccion = detail.direccion
                telefono = detail.telefono
            } catch (e: Exception) {
                error = "Error al cargar el restaurante"
            } finally {
                isLoading = false
            }
        }
    }

    fun save(id: Long) {
        if (nombre.isBlank()) {
            error = "El nombre es obligatorio"
            return
        }
        viewModelScope.launch {
            try {
                isSaving = true
                // Solo se envían los campos editables (PATCH parcial).
                val updates = RestaurantUpdateRequest(
                    nombre = nombre.trim(),
                    tipoComida = tipoComida.trim(),
                    direccion = direccion.trim(),
                    telefono = telefono.trim()
                )
                val response = service.patchRestaurant(id, updates)
                if (response.isSuccessful) {
                    updateSuccess = true
                    error = null
                } else {
                    error = "No se pudo guardar (${response.code()})"
                }
            } catch (e: Exception) {
                error = "Error de conexión al guardar"
            } finally {
                isSaving = false
            }
        }
    }
}
