package com.chiris.app.restaurant_rate.ui.restaurant.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chiris.app.restaurant_rate.data.model.RestaurantRequest

@Composable
fun CreateRestaurantScreen(
    navController: NavController,
    viewModel: RestaurantCreateViewModel = viewModel()
) {

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.createSuccess) {
        if (viewModel.createSuccess) {

            val listEntry = navController.previousBackStackEntry
            if (listEntry != null) {
                val currentValue = listEntry.savedStateHandle.get<Int>("refreshTrigger") ?: 0
                listEntry.savedStateHandle["refreshTrigger"] = currentValue + 1
            }
            viewModel.resetCreateState()
            navController.popBackStack()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevo Restaurante", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo comida") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.createRestaurant(
                    RestaurantRequest(
                        nombre = nombre,
                        tipoComida = tipo,
                        direccion = direccion,
                        telefono = telefono
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}