package com.chiris.app.restaurant_rate.data.model

// Rol del usuario. El backend lo serializa como enum en texto ("ADMIN" / "USER"),
// que es exactamente como Gson (de)serializa un enum de Kotlin por su nombre.
enum class Rol {
    ADMIN,
    USER
}

// Respuesta del backend (UsuarioDTO). El password nunca viaja de vuelta (write-only en la API),
// por eso no está en este modelo.
data class Usuario(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: Rol
)

// Alta de usuario (POST). El rol es opcional: si no se envía, el backend usa USER por defecto.
data class UsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: Rol? = null
)

// Edición parcial (PATCH): los campos nulos no se envían/actualizan en el backend.
// El password solo se cambia si se envía un valor no vacío.
data class UsuarioUpdateRequest(
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val rol: Rol? = null
)
