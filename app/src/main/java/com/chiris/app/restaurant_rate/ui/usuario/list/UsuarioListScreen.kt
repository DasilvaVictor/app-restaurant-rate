package com.chiris.app.restaurant_rate.ui.usuario.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chiris.app.restaurant_rate.data.model.Usuario
import com.chiris.app.restaurant_rate.ui.navigation.Screen
import com.chiris.app.restaurant_rate.ui.usuario.list.component.UsuarioItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioListScreen(
    navController: NavController,
    viewModel: UsuarioListViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var usuarioToDelete by remember { mutableStateOf<Usuario?>(null) }

    // Recarga al volver a la pantalla (p.ej. tras crear/editar un usuario).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUsuarios()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Usuarios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            // El FAB de alta solo tiene sentido para un ADMIN (que sí ve la lista).
            if (!viewModel.forbidden) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateUsuario.route) }
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Nuevo usuario")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                viewModel.forbidden -> {
                    CenteredMessage(
                        text = "No tienes permisos para gestionar usuarios.\nEsta sección es solo para administradores."
                    )
                }

                viewModel.error != null -> {
                    CenteredMessage(text = viewModel.error!!)
                }

                viewModel.usuarios.isEmpty() -> {
                    CenteredMessage(text = "No hay usuarios registrados.")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = viewModel.usuarios, key = { it.id }) { usuario ->
                            UsuarioItem(
                                usuario = usuario,
                                onEdit = {
                                    navController.navigate(Screen.EditUsuario.createRoute(it.id))
                                },
                                onDelete = {
                                    usuarioToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteDialog && usuarioToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    usuarioToDelete = null
                },
                title = { Text("Eliminar usuario") },
                text = { Text("¿Estás seguro de que quieres eliminar a ${usuarioToDelete?.nombre}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            usuarioToDelete?.let { viewModel.deleteUsuario(it.id) }
                            showDeleteDialog = false
                            usuarioToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        usuarioToDelete = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun BoxScope.CenteredMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxWidth()
            .padding(32.dp)
    )
}
