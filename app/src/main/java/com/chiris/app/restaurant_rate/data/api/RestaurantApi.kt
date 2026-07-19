package com.chiris.app.restaurant_rate.data.api

import com.chiris.app.restaurant_rate.data.model.RestaurantDetail
import com.chiris.app.restaurant_rate.utils.Constants
import com.chiris.app.restaurant_rate.data.model.RestaurantList
import com.chiris.app.restaurant_rate.data.model.RestaurantRequest
import com.chiris.app.restaurant_rate.data.model.RestaurantResponse
import com.chiris.app.restaurant_rate.data.model.RestaurantUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApi {
    // query opcional: si es null/vacío el backend devuelve todos los restaurantes,
    // si trae texto filtra por nombre o tipo de comida (búsqueda server-side).
    @GET(Constants.OBJECTS_PATH)
    suspend fun getAllRestaurants(
        @Query("query") query: String? = null
    ): List<RestaurantList>

    @POST(Constants.OBJECTS_PATH)
    suspend fun createRestaurant(
        @Body request: RestaurantRequest
    ): Response<RestaurantResponse>

    @GET("api/v1/restaurantes/{id}")
    suspend fun getRestaurantDetail(
        @Path("id") id: Long
    ): RestaurantDetail

    // PATCH para actualizar parcialmente un restaurante.
    // Nota: Retrofit no admite Map<String, Any> como @Body (genera un wildcard);
    // por eso se usa un DTO tipado.
    @PATCH("api/v1/restaurantes/{id}")
    suspend fun patchRestaurant(
        @Path("id") id: Long,
        @Body request: RestaurantUpdateRequest
    ): Response<RestaurantResponse>

    // DELETE para eliminar un restaurante
    @DELETE("api/v1/restaurantes/{id}")
    suspend fun deleteRestaurant(
        @Path("id") id: Long
    ): Response<Unit>
}