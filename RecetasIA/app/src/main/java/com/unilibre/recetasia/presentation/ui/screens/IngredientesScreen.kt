package com.unilibre.recetasia.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unilibre.recetasia.presentation.viewmodel.RecetasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientesScreen(
    viewModel: RecetasViewModel,
    onVolver: () -> Unit,
    onGenerarRecetas: () -> Unit
) {
    val ingredientes by viewModel.ingredientes.collectAsState()
    var nuevoIngrediente by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredientes detectados") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        viewModel.generarRecetas()
                        onGenerarRecetas()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = ingredientes.isNotEmpty()
                ) {
                    Text("✨ Generar recetas con IA")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    "Edita la lista o agrega ingredientes manualmente:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nuevoIngrediente,
                        onValueChange = { nuevoIngrediente = it },
                        label = { Text("Agregar ingrediente") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(onClick = {
                        viewModel.agregarIngrediente(nuevoIngrediente)
                        nuevoIngrediente = ""
                    }) {
                        Icon(Icons.Default.Add, "Agregar")
                    }
                }
            }

            if (ingredientes.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center) {
                        Text("No se detectaron ingredientes.\nAgrega uno manualmente.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            } else {
                items(ingredientes) { ingrediente ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🥗 $ingrediente", style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = { viewModel.eliminarIngrediente(ingrediente) }) {
                                Icon(Icons.Default.Close, "Eliminar",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}