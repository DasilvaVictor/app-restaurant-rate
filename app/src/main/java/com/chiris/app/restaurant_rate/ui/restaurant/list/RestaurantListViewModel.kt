package com.chiris.app.restaurant_rate.ui.restaurant.list

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiris.app.restaurant_rate.data.api.RestaurantApi
import com.chiris.app.restaurant_rate.data.model.RestaurantList
import com.chiris.app.restaurant_rate.data.model.RestaurantUpdateRequest
import com.chiris.app.restaurant_rate.data.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestaurantListViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Lista original de restaurantes tal como llega del backend (sin búsqueda ni orden)
    private var originalRestaurants by mutableStateOf<List<RestaurantList>>(emptyList())

    // Lista mostrada actualmente (con búsqueda + orden aplicados)
    var restaurants by mutableStateOf<List<RestaurantList>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Texto de búsqueda actual (se restaura tras muerte del proceso vía SavedStateHandle)
    var searchQuery by mutableStateOf(savedStateHandle[KEY_SEARCH] ?: "")
        private set

    // Orden actual (se persiste como nombre del enum en SavedStateHandle)
    var currentSortType by mutableStateOf(
        savedStateHandle.get<String>(KEY_SORT)?.let { runCatching { SortType.valueOf(it) }.getOrNull() }
            ?: SortType.NONE
    )
        private set

    private val service = ApiClient.retrofit.create(RestaurantApi::class.java)

    // Job de la búsqueda para poder cancelar la petición anterior mientras el usuario escribe (debounce)
    private var searchJob: Job? = null

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Tipos de ordenamiento. La etiqueta vive en el propio enum para no duplicarla en la UI.
    enum class SortType(val label: String) {
        NONE("Sin filtro"),
        BEST_RATED("Mejor calificados"),
        WORST_RATED("Peor calificados"),
        ALPHABETICAL_AZ("A - Z"),
        ALPHABETICAL_ZA("Z - A")
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            try {
                // La búsqueda se resuelve en el backend enviando el query actual.
                val query = searchQuery.trim().ifEmpty { null }
                originalRestaurants = service.getAllRestaurants(query)
                error = null
                applyFilters()
            } catch (e: Exception) {
                error = "Error cargando restaurantes"
            }
        }
    }

    // Actualizar el texto de búsqueda: se persiste el texto y se dispara la búsqueda
    // server-side con un pequeño debounce para no lanzar una petición por cada tecla.
    fun onSearchQueryChange(query: String) {
        searchQuery = query
        savedStateHandle[KEY_SEARCH] = query

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            loadRestaurants()
        }
    }

    // Aplicar ordenamiento
    fun applySort(sortType: SortType) {
        currentSortType = sortType
        savedStateHandle[KEY_SORT] = sortType.name
        applyFilters()
    }

    // Limpiar solo el ordenamiento (la búsqueda se conserva)
    fun clearFilters() {
        currentSortType = SortType.NONE
        savedStateHandle[KEY_SORT] = SortType.NONE.name
        applyFilters()
    }

    // Calcula la lista visible aplicando solo el ordenamiento.
    // El filtrado por búsqueda ahora lo resuelve el backend (server-side).
    private fun applyFilters() {
        val filtered = originalRestaurants

        restaurants = when (currentSortType) {
            SortType.BEST_RATED -> filtered.sortedByDescending { it.calificacionPromedio }
            SortType.WORST_RATED -> filtered.sortedBy { it.calificacionPromedio }
            SortType.ALPHABETICAL_AZ -> filtered.sortedBy { it.nombre.lowercase() }
            SortType.ALPHABETICAL_ZA -> filtered.sortedByDescending { it.nombre.lowercase() }
            SortType.NONE -> filtered
        }
    }

    // Texto descriptivo del orden actual (usa la etiqueta del enum)
    fun getSortTypeText(): String = currentSortType.label

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    // Editar restaurante (actualización parcial)
    fun patchRestaurant(restaurantId: Long, updates: RestaurantUpdateRequest) {
        viewModelScope.launch {
            try {
                val response = service.patchRestaurant(restaurantId, updates)
                if (response.isSuccessful) {
                    loadRestaurants() // Recargar lista después de actualizar
                    _snackbarMessage.value = "Restaurante actualizado correctamente"
                } else {
                    error = "Error al actualizar restaurante: ${response.code()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión al actualizar"
            }
        }
    }

    // Eliminar restaurante
    fun deleteRestaurant(restaurantId: Long) {
        viewModelScope.launch {
            try {
                val response = service.deleteRestaurant(restaurantId)
                if (response.isSuccessful) {
                    loadRestaurants() // Recargar lista después de eliminar
                    _snackbarMessage.value = "Restaurante eliminado correctamente"
                } else {
                    error = "Error al eliminar restaurante: ${response.code()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión al eliminar"
            }
        }
    }

    private companion object {
        const val KEY_SEARCH = "search_query"
        const val KEY_SORT = "sort_type"
        const val SEARCH_DEBOUNCE_MS = 350L
    }
}
