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
}