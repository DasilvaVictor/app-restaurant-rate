package com.chiris.app.restaurant_rate.ui.restaurant.create

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.RestaurantApi
import com.chiris.app.restaurant_rate.data.model.RestaurantRequest
import com.chiris.app.restaurant_rate.data.model.RestaurantResponse
import com.chiris.app.restaurant_rate.data.network.ApiClient
import kotlinx.coroutines.launch

class RestaurantCreateViewModel : ViewModel() {
    var createSuccess by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    private val service = ApiClient.retrofit.create(RestaurantApi::class.java)

    fun resetCreateState() {
        createSuccess = false
    }

    fun createRestaurant(
        restaurantRequest: RestaurantRequest
    ) {
        viewModelScope.launch {
            try {
                val restaurantResponse = service.createRestaurant(restaurantRequest)

                if (restaurantResponse.isSuccessful) {
                    val restaurant: RestaurantResponse? = restaurantResponse.body()

                    if (restaurant != null) {
                        createSuccess = true
                        error = null
                    } else {
                        error = "Respuesta vacía al crear restaurante"
                    }

                } else {
                    error = "Error al crear restaurante"
                }

            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }
}