package com.chiris.app.restaurant_rate.ui.restaurant.list.common// ui/restaurant/components/RestaurantFiltersBottomSheet.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chiris.app.restaurant_rate.ui.restaurant.list.RestaurantListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantFiltersBottomSheet(
    currentSortType: RestaurantListViewModel.SortType,
    onSortSelected: (RestaurantListViewModel.SortType) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.width(40.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) {}
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Text(
                text = "Ordenar restaurantes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            // Subtítulo
            Text(
                text = "Calificación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Opción: Mejor calificados
            SortOptionItem(
                text = "⭐ Mejor calificados",
                description = "Los restaurantes con mayor puntuación primero",
                isSelected = currentSortType == RestaurantListViewModel.SortType.BEST_RATED,
                onClick = { onSortSelected(RestaurantListViewModel.SortType.BEST_RATED) }
            )

            // Opción: Peor calificados
            SortOptionItem(
                text = "⭐ Peor calificados",
                description = "Los restaurantes con menor puntuación primero",
                isSelected = currentSortType == RestaurantListViewModel.SortType.WORST_RATED,
                onClick = { onSortSelected(RestaurantListViewModel.SortType.WORST_RATED) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Separador
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Subtítulo
            Text(
                text = "Orden alfabético",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            // Opción: A-Z
            SortOptionItem(
                text = "A - Z",
                description = "Orden alfabético ascendente (de la A a la Z)",
                isSelected = currentSortType == RestaurantListViewModel.SortType.ALPHABETICAL_AZ,
                onClick = { onSortSelected(RestaurantListViewModel.SortType.ALPHABETICAL_AZ) }
            )

            // Opción: Z-A
            SortOptionItem(
                text = "Z - A",
                description = "Orden alfabético descendente (de la Z a la A)",
                isSelected = currentSortType == RestaurantListViewModel.SortType.ALPHABETICAL_ZA,
                onClick = { onSortSelected(RestaurantListViewModel.SortType.ALPHABETICAL_ZA) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Limpiar filtros (solo visible si hay filtro activo)
                if (currentSortType != RestaurantListViewModel.SortType.NONE) {
                    OutlinedButton(
                        onClick = {
                            onClearFilters()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Limpiar filtros")
                    }
                }

                // Botón Aplicar/Cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(if (currentSortType != RestaurantListViewModel.SortType.NONE) 1f else 1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Aplicar")
                }
            }

            // Indicador de filtro actual
            if (currentSortType != RestaurantListViewModel.SortType.NONE) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtro activo: ${currentSortType.label}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        IconButton(
                            onClick = {
                                onClearFilters()
                                onDismiss()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar filtro",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortOptionItem(
    text: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}