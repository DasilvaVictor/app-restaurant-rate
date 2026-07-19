package com.chiris.app.restaurant_rate.ui.restaurant.detail

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.RestaurantApi
import com.chiris.app.restaurant_rate.data.api.ReviewApi
import com.chiris.app.restaurant_rate.data.model.RestaurantDetail
import com.chiris.app.restaurant_rate.data.model.ReviewRequest
import com.chiris.app.restaurant_rate.data.model.ReviewUpdateRequest
import com.chiris.app.restaurant_rate.data.network.ApiClient
import com.chiris.app.restaurant_rate.data.network.TokenManager
import kotlinx.coroutines.launch

class RestaurantDetailViewModel : ViewModel() {

    var restaurant by mutableStateOf<RestaurantDetail?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Mensaje transitorio para mostrar en un Snackbar.
    var message by mutableStateOf<String?>(null)

    // true cuando el restaurante fue eliminado, para que la pantalla vuelva atrás.
    var restaurantDeleted by mutableStateOf(false)
        private set

    // Id del usuario autenticado: sirve para saber qué reseñas puede editar/eliminar.
    val currentUserId: Long?
        get() = TokenManager.currentUserId

    private val service = ApiClient.retrofit.create(RestaurantApi::class.java)
    private val reviewService = ApiClient.retrofit.create(ReviewApi::class.java)

    fun loadRestaurant(id: Long) {
        viewModelScope.launch {
            try {
                isLoading = true
                restaurant = service.getRestaurantDetail(id)
            } catch (e: Exception) {
                message = "Error al cargar el detalle"
            } finally {
                isLoading = false
            }
        }
    }

    fun consumeMessage() {
        message = null
    }

    // ---------- Reseñas ----------

    fun createReview(
        comentario: String,
        calificacion: Double,
        restaurantId: Long
    ) {
        viewModelScope.launch {
            try {
                val response = reviewService.createReview(
                    ReviewRequest(
                        comentario = comentario,
                        calificacion = calificacion,
                        idRestaurant = restaurantId
                    )
                )
                if (response.isSuccessful) {
                    message = "Reseña publicada"
                    loadRestaurant(restaurantId)
                } else {
                    message = "No se pudo publicar la reseña (${response.code()})"
                }
            } catch (e: Exception) {
                message = "Error al crear la reseña"
            }
        }
    }

    fun updateReview(
        reviewId: Long,
        comentario: String,
        calificacion: Double,
        restaurantId: Long
    ) {
        viewModelScope.launch {
            try {
                val response = reviewService.updateReview(
                    reviewId,
                    ReviewUpdateRequest(comentario = comentario, calificacion = calificacion)
                )
                if (response.isSuccessful) {
                    message = "Reseña actualizada"
                    loadRestaurant(restaurantId)
                } else if (response.code() == 403) {
                    message = "Solo puedes editar tus propias reseñas"
                } else {
                    message = "No se pudo actualizar la reseña (${response.code()})"
                }
            } catch (e: Exception) {
                message = "Error al actualizar la reseña"
            }
        }
    }

    fun deleteReview(reviewId: Long, restaurantId: Long) {
        viewModelScope.launch {
            try {
                val response = reviewService.deleteReview(reviewId)
                if (response.isSuccessful) {
                    message = "Reseña eliminada"
                    loadRestaurant(restaurantId)
                } else if (response.code() == 403) {
                    message = "Solo puedes eliminar tus propias reseñas"
                } else {
                    message = "No se pudo eliminar la reseña (${response.code()})"
                }
            } catch (e: Exception) {
                message = "Error al eliminar la reseña"
            }
        }
    }

    // ---------- Restaurante ----------

    fun deleteRestaurant(restaurantId: Long) {
        viewModelScope.launch {
            try {
                val response = service.deleteRestaurant(restaurantId)
                if (response.isSuccessful) {
                    restaurantDeleted = true
                } else {
                    message = "No se pudo eliminar el restaurante (${response.code()})"
                }
            } catch (e: Exception) {
                message = "Error al eliminar el restaurante"
            }
        }
    }
}
