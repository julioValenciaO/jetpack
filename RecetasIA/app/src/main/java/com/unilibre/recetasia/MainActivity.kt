package com.unilibre.recetasia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unilibre.recetasia.presentation.ui.screens.*
import com.unilibre.recetasia.presentation.ui.theme.RecetasTheme
import com.unilibre.recetasia.presentation.viewmodel.RecetasViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecetasTheme {
                val navController = rememberNavController()
                val viewModel: RecetasViewModel = hiltViewModel()

                NavHost(navController, startDestination = "inicio") {
                    composable("inicio") {
                        InicioScreen(
                            viewModel,
                            onIrCamara = { navController.navigate("camara") },
                            onVerDetalle = { navController.navigate("detalle") }
                        )
                    }
                    composable("camara") {
                        CamaraScreen(
                            viewModel,
                            onVolver = { navController.popBackStack() },
                            onIngredientesDetectados = { navController.navigate("ingredientes") }
                        )
                    }
                    composable("ingredientes") {
                        IngredientesScreen(
                            viewModel,
                            onVolver = { navController.popBackStack() },
                            onGenerarRecetas = { navController.navigate("recetas") }
                        )
                    }
                    composable("recetas") {
                        RecetasIAScreen(
                            viewModel,
                            onVolver = { navController.popBackStack() },
                            onVerDetalle = { navController.navigate("detalle") }
                        )
                    }
                    composable("detalle") {
                        DetalleRecetaScreen(
                            viewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}