package com.chiris.app.restaurant_rate.ui.restaurant.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chiris.app.restaurant_rate.data.model.RestaurantList
import com.chiris.app.restaurant_rate.ui.navigation.Screen
import com.chiris.app.restaurant_rate.ui.restaurant.list.common.ScreenTitle
import com.chiris.app.restaurant_rate.ui.restaurant.list.componet.AddRestaurantFab
import com.chiris.app.restaurant_rate.ui.restaurant.list.componet.RestaurantListado
import com.chiris.app.restaurant_rate.ui.restaurant.list.componet.SearchBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.chiris.app.restaurant_rate.ui.restaurant.list.common.RestaurantFiltersBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: RestaurantListViewModel = viewModel()
) {
    val restaurants = viewModel.restaurants
    var showFilterBottomSheet by rememberSaveable { mutableStateOf(false) }
    val currentSortType = viewModel.currentSortType
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var restaurantToDelete by remember { mutableStateOf<RestaurantList?>(null) }

    HandleRefresh(navController, viewModel)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadRestaurants()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            // TopBar con botón de filtro
            TopAppBar(
                title = {
                    Text(
                        text = "Restaurantes",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    // Botón de filtro/ordenar
                    IconButton(
                        onClick = { showFilterBottomSheet = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (currentSortType != RestaurantListViewModel.SortType.NONE)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Ordenar restaurantes"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = { AddRestaurantFab(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (currentSortType != RestaurantListViewModel.SortType.NONE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { viewModel.clearFilters() },
                        label = {
                            Text("${viewModel.getSortTypeText()} ✕")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
            MainContent(
                modifier = Modifier.weight(1f),
                restaurants = restaurants,
                searchText = viewModel.searchQuery,
                onSearchChange = viewModel::onSearchQueryChange,
                onRestaurantClick = {
                    navController.navigate(Screen.Detail.createRoute(it.id))
                },
                onEditRestaurant = { restaurant ->
                    navController.navigate(Screen.EditRestaurant.createRoute(restaurant.id))
                },
                onDeleteRestaurant = { restaurant ->
                    restaurantToDelete = restaurant
                    showDeleteDialog = true
                }
            )
        }

        if (showDeleteDialog && restaurantToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    restaurantToDelete = null
                },
                title = { Text("Eliminar restaurante") },
                text = { Text("¿Estás seguro de que quieres eliminar ${restaurantToDelete?.nombre}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            restaurantToDelete?.let {
                                viewModel.deleteRestaurant(it.id)
                            }
                            showDeleteDialog = false
                            restaurantToDelete = null
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
                        restaurantToDelete = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    if (showFilterBottomSheet) {
        RestaurantFiltersBottomSheet(
            currentSortType = currentSortType,
            onSortSelected = { sortType ->
                viewModel.applySort(sortType)
                showFilterBottomSheet = false
            },
            onClearFilters = {
                viewModel.clearFilters()
                showFilterBottomSheet = false
            },
            onDismiss = {
                showFilterBottomSheet = false
            }
        )
    }
}

@Composable
private fun HandleRefresh(
    navController: NavController,
    viewModel: RestaurantListViewModel
) {
    val backStackEntry = navController.currentBackStackEntry

    val refreshTrigger by backStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refreshTrigger", 0)
        ?.collectAsState() ?: remember { mutableIntStateOf(0) }

    LaunchedEffect(refreshTrigger, backStackEntry) {
        if (refreshTrigger > 0) {
            println("🔄 Recargando restaurantes... Trigger: $refreshTrigger")
            viewModel.loadRestaurants()
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier,
    restaurants: List<RestaurantList>,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onRestaurantClick: (RestaurantList) -> Unit,
    onEditRestaurant: (RestaurantList) -> Unit,
    onDeleteRestaurant: (RestaurantList) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        ScreenTitle()

        SearchBar(
            query = searchText,
            onQueryChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        if (restaurants.isEmpty() && searchText.isNotBlank()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron restaurantes para \"$searchText\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            RestaurantListado(
                restaurants = restaurants,
                onClick = onRestaurantClick,
                modifier = Modifier.weight(1f),
                onEdit = onEditRestaurant,
                onDelete = onDeleteRestaurant,
            )
        }
    }
}