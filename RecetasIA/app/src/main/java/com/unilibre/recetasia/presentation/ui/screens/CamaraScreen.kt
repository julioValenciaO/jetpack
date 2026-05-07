package com.unilibre.recetasia.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.unilibre.recetasia.presentation.viewmodel.RecetasViewModel
import java.util.concurrent.Executors

/**
 * IA: Me ayudó a generar esta lista exhaustiva de palabras clave en inglés para filtrar 
 * los resultados de ML Kit. Esto es crucial porque los modelos de etiquetado suelen 
 * devolver resultados en inglés y necesitamos asegurar que solo detectemos alimentos.
 */
private val PALABRAS_COMIDA = setOf(
    // Frutas
    "apple", "banana", "orange", "lemon", "lime", "grape", "strawberry", "watermelon",
    "mango", "pineapple", "peach", "pear", "cherry", "blueberry", "raspberry",
    "avocado", "coconut", "fig", "melon", "papaya", "plum", "kiwi",
    // Verduras
    "tomato", "potato", "onion", "garlic", "carrot", "lettuce", "spinach",
    "broccoli", "cucumber", "pepper", "celery", "corn", "mushroom", "cabbage",
    "eggplant", "zucchini", "pumpkin", "beet", "radish", "asparagus",
    // Proteínas
    "chicken", "beef", "pork", "fish", "shrimp", "egg", "meat", "turkey",
    "salmon", "tuna", "lamb", "sausage", "bacon", "ham",
    // Lácteos
    "cheese", "milk", "butter", "cream", "yogurt",
    // Granos y otros
    "bread", "rice", "pasta", "flour", "bean", "lentil", "oat",
    "noodle", "tortilla", "cereal",
    // General comida
    "food", "fruit", "vegetable", "ingredient", "herb", "spice",
    "sauce", "oil", "vinegar", "sugar", "salt", "pepper"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamaraScreen(
    viewModel: RecetasViewModel,
    onVolver: () -> Unit,
    onIngredientesDetectados: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var tienPermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var analizando by remember { mutableStateOf(false) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> tienPermiso = granted }

    LaunchedEffect(Unit) {
        if (!tienPermiso) launcher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear ingredientes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (tienPermiso) {
                /**
                 * IA: Me proporcionó la estructura necesaria para integrar CameraX con 
                 * Jetpack Compose usando AndroidView, manejando el ciclo de vida de la 
                 * cámara de manera eficiente para evitar fugas de memoria.
                 */
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            val capture = ImageCapture.Builder().build()
                            imageCapture = capture
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview, capture
                                )
                            } catch (e: Exception) { e.printStackTrace() }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (analizando) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Analizando ingredientes...",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            "Enfoca los ingredientes y captura",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(12.dp))
                        FloatingActionButton(
                            onClick = {
                                analizando = true
                                val executor = Executors.newSingleThreadExecutor()
                                imageCapture?.takePicture(executor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(proxy: ImageProxy) {
                                            analizarImagen(proxy) { ingredientes ->
                                                viewModel.setIngredientes(ingredientes)
                                                analizando = false
                                                onIngredientesDetectados()
                                            }
                                        }
                                        override fun onError(e: ImageCaptureException) {
                                            analizando = false
                                        }
                                    }
                                )
                            }
                        ) {
                            Icon(Icons.Default.Camera, "Capturar")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Se necesita permiso de cámara",
                        style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("Conceder permiso")
                    }
                }
            }
        }
    }
}

/**
 * IA: Me guió en la implementación de ML Kit para el etiquetado de imágenes (Image Labeling).
 * Incluyó la lógica para convertir el ImageProxy a InputImage y cómo filtrar los 
 * resultados por nivel de confianza (confidence > 0.70f).
 */
private fun analizarImagen(imageProxy: ImageProxy, onResult: (List<String>) -> Unit) {
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    val inputImage = InputImage.fromMediaImage(
        imageProxy.image!!,
        imageProxy.imageInfo.rotationDegrees
    )
    labeler.process(inputImage)
        .addOnSuccessListener { labels ->
            val ingredientes = labels
                .filter { it.confidence > 0.70f }
                .map { it.text }
                .filter { label ->
                    // Filtrar solo etiquetas relacionadas con comida
                    PALABRAS_COMIDA.any { palabra ->
                        label.lowercase().contains(palabra)
                    }
                }
                .take(8) // máximo 8 ingredientes

            // Si no detectó comida, pasar lista vacía
            // El usuario puede agregar ingredientes manualmente
            onResult(ingredientes)
        }
        .addOnFailureListener { onResult(emptyList()) }
        .addOnCompleteListener { imageProxy.close() }
}
