package com.chiris.app.restaurant_rate.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object CreateRestaurant : Screen("create_restaurant")

    object Detail : Screen("detail/{id}") {
        fun createRoute(id: Long) = "detail/$id"
    }

    object EditRestaurant : Screen("edit_restaurant/{id}") {
        fun createRoute(id: Long) = "edit_restaurant/$id"
    }

    // Gestión de usuarios (solo ADMIN en el backend).
    object Usuarios : Screen("usuarios")
    object CreateUsuario : Screen("create_usuario")

    object EditUsuario : Screen("edit_usuario/{id}") {
        fun createRoute(id: Long) = "edit_usuario/$id"
    }
}