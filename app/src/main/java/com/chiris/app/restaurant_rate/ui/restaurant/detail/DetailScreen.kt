package com.chiris.app.restaurant_rate.ui.restaurant.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chiris.app.restaurant_rate.data.model.ReviewResponse
import com.chiris.app.restaurant_rate.ui.restaurant.detail.component.RatingBar
import com.chiris.app.restaurant_rate.ui.restaurant.detail.component.RatingDisplay
import com.chiris.app.restaurant_rate.ui.restaurant.detail.component.ReviewItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: Long,
    navController: NavController,
    viewModel: RestaurantDetailViewModel = viewModel()
) {
    val restaurant = viewModel.restaurant
    val snackbarHostState = remember { SnackbarHostState() }

    var comentario by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    var showDeleteRestaurantDialog by remember { mutableStateOf(false) }
    var reviewToDelete by remember { mutableStateOf<ReviewResponse?>(null) }
    var reviewToEdit by remember { mutableStateOf<ReviewResponse?>(null) }

    LaunchedEffect(id) {
        viewModel.loadRestaurant(id)
    }

    // Mensajes transitorios -> Snackbar
    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    // Restaurante eliminado -> avisar a la lista y volver
    LaunchedEffect(viewModel.restaurantDeleted) {
        if (viewModel.restaurantDeleted) {
            navController.previousBackStackEntry?.savedStateHandle?.let { handle ->
                val current = handle.get<Int>("refreshTrigger") ?: 0
                handle["refreshTrigger"] = current + 1
            }
            navController.popBackStack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.nombre ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (restaurant != null) {
                        IconButton(onClick = {
                            navController.navigate("edit_restaurant/${restaurant.id}")
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar restaurante")
                        }
                        IconButton(onClick = { showDeleteRestaurantDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar restaurante",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->

        if (restaurant == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Tarjeta de información ---
            item {
                RestaurantInfoCard(
                    nombre = restaurant.nombre,
                    promedio = restaurant.calificacionPromedio,
                    tipoComida = restaurant.tipoComida,
                    direccion = restaurant.direccion,
                    telefono = restaurant.telefono,
                    totalResenas = restaurant.resenas.size
                )
            }

            // --- Formulario nueva reseña ---
            item {
                NewReviewCard(
                    comentario = comentario,
                    onComentarioChange = { comentario = it },
                    rating = rating,
                    onRatingChange = { rating = it },
                    onSubmit = {
                        viewModel.createReview(
                            comentario = comentario.trim(),
                            calificacion = rating.toDouble(),
                            restaurantId = id
                        )
                        comentario = ""
                        rating = 0
                    }
                )
            }

            // --- Encabezado reseñas ---
            item {
                Text(
                    text = "Reseñas (${restaurant.resenas.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (restaurant.resenas.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay reseñas. ¡Sé el primero en opinar!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(restaurant.resenas, key = { it.id }) { review ->
                    ReviewItem(
                        review = review,
                        isOwner = review.idUsuario == viewModel.currentUserId,
                        onEdit = { reviewToEdit = review },
                        onDelete = { reviewToDelete = review }
                    )
                }
            }
        }
    }

    // --- Diálogo eliminar restaurante ---
    if (showDeleteRestaurantDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteRestaurantDialog = false },
            title = { Text("Eliminar restaurante") },
            text = { Text("¿Seguro que quieres eliminar \"${restaurant?.nombre}\"? Se borrarán también sus reseñas.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteRestaurantDialog = false
                        viewModel.deleteRestaurant(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteRestaurantDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // --- Diálogo eliminar reseña ---
    reviewToDelete?.let { review ->
        AlertDialog(
            onDismissRequest = { reviewToDelete = null },
            title = { Text("Eliminar reseña") },
            text = { Text("¿Seguro que quieres eliminar tu reseña?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReview(review.id, id)
                        reviewToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { reviewToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    // --- Diálogo editar reseña ---
    reviewToEdit?.let { review ->
        EditReviewDialog(
            review = review,
            onDismiss = { reviewToEdit = null },
            onConfirm = { nuevoComentario, nuevaCalificacion ->
                viewModel.updateReview(
                    reviewId = review.id,
                    comentario = nuevoComentario,
                    calificacion = nuevaCalificacion,
                    restaurantId = id
                )
                reviewToEdit = null
            }
        )
    }
}

@Composable
private fun RestaurantInfoCard(
    nombre: String,
    promedio: Double,
    tipoComida: String,
    direccion: String,
    telefono: String,
    totalResenas: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingDisplay(promedio)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = String.format("%.1f", promedio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "  ·  $totalResenas reseña${if (totalResenas == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(Icons.Default.Fastfood, tipoComida)
            InfoRow(Icons.Default.LocationOn, direccion)
            InfoRow(Icons.Default.Phone, telefono)
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    if (text.isBlank()) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun NewReviewCard(
    comentario: String,
    onComentarioChange: (String) -> Unit,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RateReview, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    "Agregar reseña",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = comentario,
                onValueChange = onComentarioChange,
                label = { Text("Comentario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tu calificación", style = MaterialTheme.typography.labelLarge)
            RatingBar(rating = rating, onRatingChanged = onRatingChange)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSubmit,
                enabled = comentario.isNotBlank() && rating > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar reseña")
            }
        }
    }
}

@Composable
private fun EditReviewDialog(
    review: ReviewResponse,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var comentario by remember { mutableStateOf(review.comentario) }
    var rating by remember { mutableIntStateOf(review.calificacion.toInt()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar reseña") },
        text = {
            Column {
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Calificación", style = MaterialTheme.typography.labelLarge)
                RatingBar(rating = rating, onRatingChanged = { rating = it })
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(comentario.trim(), rating.toDouble()) },
                enabled = comentario.isNotBlank() && rating > 0
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
