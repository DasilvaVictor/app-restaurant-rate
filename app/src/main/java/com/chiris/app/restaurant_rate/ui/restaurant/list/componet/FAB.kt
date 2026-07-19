package com.chiris.app.restaurant_rate.ui.restaurant.list.componet

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chiris.app.restaurant_rate.ui.navigation.Screen

@Composable
fun AddRestaurantFab(navController: NavController) {
    FloatingActionButton(
        onClick = {
            navController.navigate(Screen.CreateRestaurant.route)
        }
    ) {
        Text("+")
    }
}