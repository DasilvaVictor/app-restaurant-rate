package com.chiris.app.restaurant_rate.data.api

import com.chiris.app.restaurant_rate.data.model.ReviewRequest
import com.chiris.app.restaurant_rate.data.model.ReviewResponse
import com.chiris.app.restaurant_rate.data.model.ReviewUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApi {
    @POST("api/v1/resenas")
    suspend fun createReview(
        @Body request: ReviewRequest
    ): Response<ReviewResponse>

    // PATCH para editar una reseña (solo el autor puede hacerlo, validado en el backend)
    @PATCH("api/v1/resenas/{id}")
    suspend fun updateReview(
        @Path("id") id: Long,
        @Body request: ReviewUpdateRequest
    ): Response<ReviewResponse>

    // DELETE para eliminar una reseña propia
    @DELETE("api/v1/resenas/{id}")
    suspend fun deleteReview(
        @Path("id") id: Long
    ): Response<Unit>
}
