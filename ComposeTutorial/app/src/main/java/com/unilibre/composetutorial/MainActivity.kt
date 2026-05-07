package com.unilibre.composetutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.unilibre.composetutorial.taller03.ui.WeatherScreen
import com.unilibre.composetutorial.ui.theme.ComposeTutorialTheme
import com.unilibre.composetutorial.ui.theme.BgDark

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark
                ) {
                    // Ejecutando Taller 03: App del Clima
                    WeatherScreen()
                    // Ejecutando Taller 02: Gestor de Tareas
                    //Taller02App()
                    // Ejecutando Taller 01: Composable Básico y Estilo Terminal
                    //Taller01App()

                }
            }
        }
    }
}
