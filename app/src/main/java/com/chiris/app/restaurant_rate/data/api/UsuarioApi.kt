package com.chiris.app.restaurant_rate.data.api

import com.chiris.app.restaurant_rate.data.model.Usuario
import com.chiris.app.restaurant_rate.data.model.UsuarioRequest
import com.chiris.app.restaurant_rate.data.model.UsuarioUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Gestión de usuarios. Todos los endpoints requieren rol ADMIN en el backend
// (ver SecurityConfig); un usuario normal recibirá 403 Forbidden.
interface UsuarioApi {

    @GET("api/v1/usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    @GET("api/v1/usuarios/{id}")
    suspend fun getUsuario(
        @Path("id") id: Long
    ): Usuario

    @POST("api/v1/usuarios")
    suspend fun createUsuario(
        @Body request: UsuarioRequest
    ): Response<Usuario>

    @PATCH("api/v1/usuarios/{id}")
    suspend fun patchUsuario(
        @Path("id") id: Long,
        @Body request: UsuarioUpdateRequest
    ): Response<Usuario>

    @DELETE("api/v1/usuarios/{id}")
    suspend fun deleteUsuario(
        @Path("id") id: Long
    ): Response<Unit>
}
