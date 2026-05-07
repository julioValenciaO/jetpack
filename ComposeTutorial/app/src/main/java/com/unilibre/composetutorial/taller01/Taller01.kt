package com.unilibre.composetutorial.taller01

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unilibre.composetutorial.R

/** 
 * TALLER 01 - Estilo Terminal 
 * La IA sugirió inicialmente una paleta hacker básica. 
 * Yo la ajusté para que el fondo sea un negro más profundo (#0D1117) y 
 * el verde sea más vibrante (#3FB950) para mejorar el contraste.
 */
private val BgDark      = Color(0xFF0D1117)
private val CardBg      = Color(0xFF161B22)
private val BubbleRecv  = Color(0xFF21262D)
private val AccentGreen = Color(0xFF3FB950) 
private val TextPrimary = Color(0xFFE6EDF3)
private val TextMuted   = Color(0xFF8B949E)

@Composable
fun TarjetaBienvenida(nombre: String, modifier: Modifier = Modifier) {
    // CORRECCIÓN : La IA generó esto con paddings de 8.dp.
    // Los aumenté a 16.dp para que no se viera tan comprimido y tenga "respiración visual".
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF30363D))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hola, $nombre!",
                style = MaterialTheme.typography.headlineMedium,
                color = AccentGreen,
                // Aplicación manual de la fuente monoespaciada para simular el entorno de terminal
                fontFamily = FontFamily.Monospace 
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido a Jetpack Compose",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun PantallaPresentacion(nombre: String, cargo: String, descripcion: String) {
    // CORRECCIÓN: La IA sugirió una Column simple. Añadí verticalArrangement = Arrangement.Center
    // para que la tarjeta de perfil se mantenga centrada y profesional en cualquier pantalla.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // CORRECCIÓN: La IA intentó cargar R.drawable.profile (inexistente).
        // Corregido al recurso real: R.drawable.profile_picture.
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(CardBg),
            contentScale = ContentScale.Crop
        )

        // Se aumentó el tamaño de los Spacers de 8.dp a 16.dp para mejorar el balance visual.
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = nombre,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = cargo,
            fontSize = 16.sp,
            color = AccentGreen,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = descripcion,
            color = TextMuted,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Acción */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6FEB)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Contactar", fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // CORRECCIÓN: La IA intentó usar Icons.Default.Twitter (no disponible).
        // Se reemplazó por Share e Info de la librería estándar para evitar errores de compilación.
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(Icons.Default.Share, "GitHub", tint = TextPrimary, modifier = Modifier.size(28.dp))
            Icon(Icons.Default.Person, "LinkedIn", tint = TextPrimary, modifier = Modifier.size(28.dp))
            Icon(Icons.Default.Info, "Web", tint = TextPrimary, modifier = Modifier.size(28.dp))
        }
    }
}

data class Message(val author: String, val body: String)

object SampleData {
    val conversationSample = listOf(
        Message("Juan", "Hola! Bienvenido a mi app de Jetpack Compose"),
        Message("Juan", "Toca cualquier mensaje para expandirlo y ver el texto completo!")
    )
}

@Composable
fun ChatBubble(msg: Message) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(CardBg),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = msg.author, color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp),
                color = BubbleRecv,
                modifier = Modifier.clickable { expanded = !expanded }.animateContentSize()
            ) {
                Text(
                    text = msg.body,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ChatScreen(messages: List<Message>) {
    // CORRECCIÓN: La IA generó este código inicialmente en MainActivity.kt.
    // Realicé una refactorización manual para extraerlo a este paquete taller01.
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BgDark).padding(16.dp)
    ) {
        item {
            TarjetaBienvenida(nombre = "Juan")
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(messages) { message ->
            ChatBubble(msg = message)
        }
    }
}
