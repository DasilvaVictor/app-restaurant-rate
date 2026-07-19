package com.chiris.app.restaurant_rate.data.network

import android.util.Base64
import androidx.compose.runtime.*
import org.json.JSONObject

object TokenManager {
    var token by mutableStateOf<String?>(null)

    // Id del usuario autenticado, decodificado del payload del JWT (claim "userId").
    // Se usa para saber qué reseñas pertenecen al usuario actual y mostrar sus acciones.
    val currentUserId: Long?
        get() = decodeClaim("userId")?.toLongOrNull()

    // Email del usuario autenticado (claim estándar "sub").
    val currentUserEmail: String?
        get() = decodeClaim("sub")

    private fun decodeClaim(claim: String): String? {
        val jwt = token ?: return null
        return try {
            val payload = jwt.split(".").getOrNull(1) ?: return null
            val decoded = String(
                Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            )
            val json = JSONObject(decoded)
            if (json.has(claim)) json.get(claim).toString() else null
        } catch (e: Exception) {
            null
        }
    }
}
