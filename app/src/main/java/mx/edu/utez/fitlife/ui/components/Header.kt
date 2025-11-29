package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Color
import mx.edu.utez.fitlife.ui.theme.PrimaryBlue

@Composable
fun Header(title:String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBlue)
            .padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        Icon(Icons.Default.Star, null, tint = Color.White)
    }
}
