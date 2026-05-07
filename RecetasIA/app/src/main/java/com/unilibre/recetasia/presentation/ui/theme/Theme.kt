package com.unilibre.recetasia.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary          = Naranja500,
    onPrimary        = Color.White,
    primaryContainer = Naranja50,
    secondary        = Verde500,
    background       = Crema,
    surface          = Color.White,
    onBackground     = GrisOscuro,
    onSurface        = GrisOscuro
)

private val DarkColors = darkColorScheme(
    primary          = Naranja200,
    onPrimary        = GrisOscuro,
    primaryContainer = Naranja500,
    secondary        = Verde500,
    background       = SuperfOscura,
    surface          = Color(0xFF2C2C2C),
    onBackground     = Color.White,
    onSurface        = Color.White
)

@Composable
fun RecetasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = RecetasTypography,
        content     = content
    )
}