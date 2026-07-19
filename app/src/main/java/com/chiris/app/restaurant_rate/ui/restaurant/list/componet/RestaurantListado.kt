package com.chiris.app.restaurant_rate.ui.restaurant.list.componet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chiris.app.restaurant_rate.data.model.RestaurantList

@Composable
fun RestaurantListado(
    restaurants: List<RestaurantList>,
    onClick: (RestaurantList) -> Unit,
    modifier: Modifier = Modifier,
    onEdit: (RestaurantList) -> Unit,
    onDelete: (RestaurantList) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = restaurants,
            key = { it.id }
        ) { restaurant ->
            RestaurantItem(
                restaurant = restaurant,
                onClick = onClick,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}