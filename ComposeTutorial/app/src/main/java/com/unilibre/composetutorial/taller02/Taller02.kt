package com.unilibre.composetutorial.taller02

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.io.Serializable

// Paleta de colores: Se suavizaron los tonos originales (verdes brillantes) a una gama azulada 
// para mejorar la accesibilidad y reducir la fatiga visual tras detectar un contraste excesivo.
private val BgDark      = Color(0xFF0F111A) 
private val CardBg      = Color(0xFF1E212E) 
private val AccentGreen = Color(0xFF4CAF50) 
private val AccentBlue  = Color(0xFF3D5AFE) 
private val AccentRed   = Color(0xFFEF5350) 
private val TextPrimary = Color(0xFFECEFF1) 
private val TextMuted   = Color(0xFF90A4AE) 
private val BorderColor = Color(0xFF37474F) 

// El modelo implementa Serializable para asegurar que el estado se mantenga 
// correctamente durante los ciclos de vida de Android.
data class Tarea(
    val id: Int,
    val titulo: String,
    val completada: Boolean = false
) : Serializable

@Composable
fun Taller02App() {
    val navController = rememberNavController()
    
    // Se corrigió el uso de 'remember' simple por 'rememberSaveable' para evitar 
    // la pérdida de la lista al rotar el dispositivo.
    var tareas by rememberSaveable { mutableStateOf(listOf<Tarea>()) }

    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            ListaTareasScreen(
                navController = navController,
                tareas = tareas,
                onTareasChange = { tareas = it }
            )
        }
        // Se definió explícitamente NavType.IntType para evitar errores de casting 
        // al recuperar el ID en la pantalla de destino.
        composable(
            route = "detalle/{tareaId}",
            arguments = listOf(navArgument("tareaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getInt("tareaId")
            val tarea = tareas.find { it.id == tareaId }
            DetalleTareaScreen(navController, tarea)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    navController: NavController,
    tareas: List<Tarea>,
    onTareasChange: (List<Tarea>) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var textoNuevaTarea by remember { mutableStateOf("") }
    var errorTexto by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { 
                    // Se cambió el lenguaje de la UI de "TASK_MANAGER" a "Gestor de Tareas"
                    // para que la interfaz sea más intuitiva y cercana al usuario.
                    Text("Gestor de Tareas", fontWeight = FontWeight.Bold) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark, titleContentColor = TextPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = AccentBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (tareas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu lista está vacía", color = TextMuted)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tareas, key = { it.id }) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onCheckedChange = { checked ->
                            onTareasChange(tareas.map {
                                if (it.id == tarea.id) it.copy(completada = checked) else it
                            })
                        },
                        onDelete = {
                            onTareasChange(tareas.filter { it.id != tarea.id })
                        },
                        onClick = { 
                            // Se corrigió una excepción de 'destination unknown' unificando 
                            // los strings de ruta en la llamada de navegación.
                            navController.navigate("detalle/${tarea.id}") 
                        }
                    )
                }
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                containerColor = CardBg,
                title = { Text("Nueva Tarea", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = textoNuevaTarea,
                            onValueChange = { 
                                textoNuevaTarea = it
                                if (it.isNotBlank()) errorTexto = null 
                            },
                            placeholder = { Text("¿Qué quieres hacer?") },
                            isError = errorTexto != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                        // Se añadió este mensaje de error para mejorar la validación visual de la entrada
                        if (errorTexto != null) {
                            Text(errorTexto!!, color = AccentRed, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (textoNuevaTarea.isBlank()) {
                            errorTexto = "Escribe un título para continuar"
                        } else {
                            val nuevaTarea = Tarea(
                                id = (tareas.maxOfOrNull { it.id } ?: 0) + 1,
                                titulo = textoNuevaTarea
                            )
                            onTareasChange(tareas + nuevaTarea)
                            textoNuevaTarea = ""
                            mostrarDialogo = false
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) {
                        Text("Guardar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaItem(
    tarea: Tarea,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) AccentRed else Color.Transparent,
                label = "delete_bg"
            )
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(color).padding(horizontal = 24.dp), contentAlignment = Alignment.CenterEnd) {
                Icon(Icons.Default.Delete, null, tint = Color.White)
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            color = CardBg,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            tonalElevation = 4.dp
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = AccentGreen)
                )
                Spacer(modifier = Modifier.width(12.dp))
                
                // Se ajustó el alcance de la animación de fade: ahora solo afecta al texto 
                // para que el Checkbox se mantenga siempre visible con opacidad completa.
                Text(
                    text = tarea.titulo,
                    color = if (tarea.completada) TextMuted else TextPrimary,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )
                
                // Se añadió un botón de eliminar directo para mejorar la accesibilidad 
                // complementando el gesto de swipe.
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = TextMuted.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(navController: NavController, tarea: Tarea?) {
    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark, titleContentColor = TextPrimary)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
            if (tarea != null) {
                Surface(color = CardBg, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, BorderColor), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ID: #${tarea.id}", color = AccentBlue, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Text(tarea.titulo, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(24.dp))
                        val status = if (tarea.completada) "COMPLETADA" to AccentGreen else "PENDIENTE" to AccentRed
                        Text(status.first, color = status.second, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
