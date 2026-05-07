package com.unilibre.recetasia.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unilibre.recetasia.presentation.ui.components.RecetaCard
import com.unilibre.recetasia.presentation.viewmodel.RecetasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    viewModel: RecetasViewModel,
    onIrCamara: () -> Unit,
    onVerDetalle: () -> Unit
) {
    val favoritas by viewModel.favoritas.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("🍽️ Recetas IA") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onIrCamara,
                icon = { Icon(Icons.Default.CameraAlt, "Cámara") },
                text = { Text("Escanear ingredientes") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (favoritas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍳", style = MaterialTheme.typography.headlineLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Toca el botón para fotografiar tus ingredientes y obtener recetas con IA",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                item { Text("Recetas guardadas", style = MaterialTheme.typography.titleLarge) }
                items(favoritas, key = { it.id }) { receta ->
                    RecetaCard(
                        receta = receta,
                        onClick = {
                            viewModel.seleccionarReceta(receta)
                            onVerDetalle()
                        },
                        onFavorita = { viewModel.guardarFavorita(receta) }
                    )
                }
            }
        }
    }
}