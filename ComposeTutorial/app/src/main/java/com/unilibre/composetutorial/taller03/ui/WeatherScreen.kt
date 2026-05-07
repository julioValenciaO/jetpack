package com.unilibre.composetutorial.taller03.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unilibre.composetutorial.taller03.data.WeatherResponse

/**
 * TALLER 03: VIEWMODEL + STATEFLOW - APP DEL CLIMA
 *
 * Implementación de una interfaz moderna para visualizar el clima en tiempo real.
 * Se utiliza animateColorAsState para transiciones suaves en el fondo según la condición climática.
 */

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cityInput by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    val condition = if (uiState is WeatherUiState.Success) {
        (uiState as WeatherUiState.Success).data.weather.firstOrNull()?.main ?: "Clear"
    } else "Clear"

    // La IA sugirió colores básicos. Yo los refiné para que el naranja sea más cálido
    // y el azul de lluvia sea un tono más profundo y elegante.
    val bgColor by animateColorAsState(
        targetValue = when (condition.lowercase()) {
            "rain", "drizzle", "thunderstorm" -> Color(0xFF1A237E)
            "clouds" -> Color(0xFF37474F)
            "snow" -> Color(0xFFE3F2FD)
            "night" -> Color(0xFF1A0033)
            else -> Color(0xFFE65100)
        },
        animationSpec = tween(1000),
        label = "bgColor"
    )

    // CORRECCIÓN: La IA aplicó el gradiente directamente en el Surface de los resultados.
    // Esto causaba cortes visuales. Se movió el gradiente al Box contenedor principal para que cubra toda la pantalla.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgColor, bgColor.copy(alpha = 0.6f))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "🌤 Clima",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                placeholder = { Text("Escribe una ciudad...", color = Color.White.copy(alpha = 0.6f)) },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.loadWeather(cityInput)
                        keyboard?.hide()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.loadWeather(cityInput)
                    keyboard?.hide()
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Gestión de estados usando la sealed class sugerida por la IA y refinada para el flujo de la app.
            when (val state = uiState) {
                is WeatherUiState.Idle -> {
                    Text("Busca una ciudad para ver el clima", color = Color.White, textAlign = TextAlign.Center)
                }
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is WeatherUiState.Success -> {
                    WeatherContent(state.data)
                }
                is WeatherUiState.Error -> {
                    Text("❌ ${state.message}", color = Color.Red, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun WeatherContent(data: WeatherResponse) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = data.city,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${data.main.temp.toInt()}°C",
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = data.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Organización de información adicional en chips para una mejor legibilidad.
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherChip(label = "💧 Humedad", value = "${data.main.humidity}%")
            WeatherChip(label = "💨 Viento", value = "${data.wind.speed} m/s")
        }
    }
}

@Composable
fun WeatherChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreen() {
    WeatherScreen()
}
