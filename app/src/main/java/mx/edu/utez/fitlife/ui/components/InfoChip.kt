package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@Composable
fun InfoChip(
    title: String,
    value: String,
    bg: Color,
    accent: Color
) {

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {

        Row(
            modifier = Modifier.padding(12.dp)
        ) {

            Icon(Icons.Default.Place, null, tint = accent)

            Spacer(Modifier.width(8.dp))

            Column {
                Text(title)
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    color = accent
                )
            }
        }
    }
}
