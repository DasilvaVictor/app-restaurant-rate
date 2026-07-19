package com.chiris.app.restaurant_rate.ui.restaurant.list.componet

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chiris.app.restaurant_rate.data.model.RestaurantList
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@SuppressLint("DefaultLocale")
@Composable
fun RestaurantItem(
    restaurant: RestaurantList,
    onClick: (RestaurantList) -> Unit,
    onEdit: (RestaurantList) -> Unit,
    onDelete: (RestaurantList) -> Unit
) {
    val editAction = SwipeAction(
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = Color.White
            )
        },
        background = MaterialTheme.colorScheme.primary,
        onSwipe = { onEdit(restaurant) }
    )

    val deleteAction = SwipeAction(
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color.White
            )
        },
        background = MaterialTheme.colorScheme.error,
        onSwipe = { onDelete(restaurant) }
    )

    SwipeableActionsBox(
        endActions = listOf(editAction, deleteAction)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(restaurant) },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(12.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = restaurant.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "⭐ ${String.format("%.1f", restaurant.calificacionPromedio)}"
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = restaurant.tipoComida,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}