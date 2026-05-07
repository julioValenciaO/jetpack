package com.unilibre.recetasia.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.unilibre.recetasia.domain.model.Receta
import com.unilibre.recetasia.presentation.ui.components.RecetaCard
import com.unilibre.recetasia.presentation.viewmodel.RecetasUiState
import com.unilibre.recetasia.presentation.viewmodel.RecetasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasIAScreen(
    viewModel: RecetasViewModel,
    onVolver: () -> Unit,
    onVerDetalle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recetas sugeridas") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is RecetasUiState.Idle -> {}

                is RecetasUiState.Cargando -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        TypewriterText("Consultando Gemini AI...")
                    }
                }

                is RecetasUiState.Exito -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            Text("✨ ${state.recetas.size} recetas generadas",
                                style = MaterialTheme.typography.titleLarge)
                        }
                        items(state.recetas, key = { it.id }) { receta ->
                            AnimatedRecetaCard(receta, viewModel, onVerDetalle)
                        }
                    }
                }

                is RecetasUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("❌ ${state.mensaje}", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.generarRecetas() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedRecetaCard(
    receta: Receta,
    viewModel: RecetasViewModel,
    onVerDetalle: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "card_alpha"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.alpha(alpha)) {
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

@Composable
fun TypewriterText(texto: String) {
    var displayedText by remember { mutableStateOf("") }
    LaunchedEffect(texto) {
        texto.forEachIndexed { i, _ ->
            displayedText = texto.substring(0, i + 1)
            kotlinx.coroutines.delay(50)
        }
    }
    Text(displayedText, style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
}