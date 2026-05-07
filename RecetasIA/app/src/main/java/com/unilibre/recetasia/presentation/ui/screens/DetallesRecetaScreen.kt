package com.unilibre.recetasia.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.unilibre.recetasia.presentation.ui.components.ChipInfo
import com.unilibre.recetasia.presentation.ui.theme.Naranja50
import com.unilibre.recetasia.presentation.ui.theme.Verde50
import com.unilibre.recetasia.presentation.viewmodel.RecetasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    viewModel: RecetasViewModel,
    onVolver: () -> Unit
) {
    val receta by viewModel.recetaSeleccionada.collectAsState()
    var esFavorita by remember { mutableStateOf(receta?.esFavorita ?: false) }

    // Animación del botón favorito
    val escala by animateFloatAsState(
        targetValue = if (esFavorita) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fav_scale"
    )

    receta?.let { r ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(r.nombre) },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                esFavorita = !esFavorita
                                viewModel.guardarFavorita(r.copy(esFavorita = esFavorita))
                            },
                            modifier = Modifier.scale(escala)
                        ) {
                            Icon(
                                if (esFavorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                "Favorita",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Chips de info
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipInfo("⏱ ${r.tiempoMinutos} min", Naranja50)
                        ChipInfo("🔥 ${r.calorias} cal", Verde50)
                        ChipInfo(r.dificultad)
                    }
                }

                // Ingredientes
                item {
                    Text("Ingredientes", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    r.ingredientes.forEach { ing ->
                        Text("• $ing", style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 2.dp))
                    }
                }

                // Pasos
                item { Text("Preparación", style = MaterialTheme.typography.titleLarge) }

                itemsIndexed(r.pasos) { index, paso ->
                    PasoCard(numero = index + 1, texto = paso)
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No se encontró la receta")
    }
}

@Composable
fun PasoCard(numero: Int, texto: String) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, delayMillis = numero * 100),
        label = "paso_alpha"
    )
    LaunchedEffect(Unit) { visible = true }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("$numero", color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(texto, style = MaterialTheme.typography.bodyLarge)
        }
    }
}