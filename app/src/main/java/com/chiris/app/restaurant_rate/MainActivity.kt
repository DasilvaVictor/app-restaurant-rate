package com.chiris.app.restaurant_rate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chiris.app.restaurant_rate.ui.theme.RestaurantRateTheme
import com.chiris.app.restaurant_rate.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestaurantRateTheme {
                AppNavigation()
            }
        }
    }
}