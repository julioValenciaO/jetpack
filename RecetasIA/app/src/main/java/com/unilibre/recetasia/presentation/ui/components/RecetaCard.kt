package com.unilibre.recetasia.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unilibre.recetasia.domain.model.Receta
import com.unilibre.recetasia.presentation.ui.theme.Naranja50
import com.unilibre.recetasia.presentation.ui.theme.Verde50

@Composable
fun RecetaCard(
    receta: Receta,
    onClick: () -> Unit,
    onFavorita: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(receta.nombre, style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f))
                IconButton(onClick = onFavorita) {
                    Icon(
                        if (receta.esFavorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorita",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipInfo("⏱ ${receta.tiempoMinutos} min", Naranja50)
                ChipInfo("🔥 ${receta.calorias} cal", Verde50)
                ChipInfo(receta.dificultad)
            }
        }
    }
}