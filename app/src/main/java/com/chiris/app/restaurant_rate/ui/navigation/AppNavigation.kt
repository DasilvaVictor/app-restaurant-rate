package com.chiris.app.restaurant_rate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chiris.app.restaurant_rate.data.network.TokenManager
import com.chiris.app.restaurant_rate.ui.login.LoginScreen
import com.chiris.app.restaurant_rate.ui.restaurant.create.CreateRestaurantScreen
import com.chiris.app.restaurant_rate.ui.restaurant.detail.DetailScreen
import com.chiris.app.restaurant_rate.ui.restaurant.edit.EditRestaurantScreen
import com.chiris.app.restaurant_rate.ui.restaurant.list.MainScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val token = TokenManager.token

    val startDestination = if (token != null) {
        Screen.Main.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }

        composable(Screen.CreateRestaurant.route) {
            CreateRestaurantScreen(navController = navController)
        }

        composable(Screen.Detail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            DetailScreen(id = id, navController = navController)
        }

        composable(Screen.EditRestaurant.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            EditRestaurantScreen(id = id, navController = navController)
        }
    }
}
