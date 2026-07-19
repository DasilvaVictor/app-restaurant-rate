package com.chiris.app.restaurant_rate.data.model

data class RestaurantList(
    val id: Long,
    val nombre: String,
    val tipoComida:String,
    val calificacionPromedio: Double )

data class RestaurantRequest(
    val nombre: String,
    val tipoComida: String,
    val direccion: String,
    val telefono: String
)

data class RestaurantResponse(
    val id: Long,
    val nombre: String,
    val tipoComida: String,
    val direccion: String,
    val telefono: String
)

// PATCH parcial: los campos nulos no se envían/actualizan en el backend.
data class RestaurantUpdateRequest(
    val nombre: String? = null,
    val tipoComida: String? = null,
    val direccion: String? = null,
    val telefono: String? = null
)

data class ReviewRequest(
    val comentario: String,
    val calificacion: Double,
    val idRestaurant: Long
)

data class ReviewUpdateRequest(
    val comentario: String,
    val calificacion: Double
)

data class ReviewResponse(
    val id: Long,
    val comentario: String,
    val calificacion: Double,
    val idUsuario: Long,
    val usuarioNombre: String
)
data class RestaurantDetail(
    val id: Long,
    val nombre: String,
    val tipoComida: String,
    val calificacionPromedio: Double,
    val direccion: String,
    val telefono: String,
    val resenas: List<ReviewResponse>
)