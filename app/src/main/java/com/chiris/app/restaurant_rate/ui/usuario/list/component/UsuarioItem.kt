package com.chiris.app.restaurant_rate.ui.usuario.list.component

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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chiris.app.restaurant_rate.data.model.Rol
import com.chiris.app.restaurant_rate.data.model.Usuario
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun UsuarioItem(
    usuario: Usuario,
    onEdit: (Usuario) -> Unit,
    onDelete: (Usuario) -> Unit
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
        onSwipe = { onEdit(usuario) }
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
        onSwipe = { onDelete(usuario) }
    )

    SwipeableActionsBox(
        endActions = listOf(editAction, deleteAction)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = usuario.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    RolChip(usuario.rol)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RolChip(rol: Rol) {
    val isAdmin = rol == Rol.ADMIN
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(rol.name) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = if (isAdmin)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            disabledLabelColor = if (isAdmin)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
