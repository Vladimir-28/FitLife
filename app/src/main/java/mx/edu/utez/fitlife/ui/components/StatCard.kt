package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatCard(title: String, value: String) {

    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
    ) {

        Column(Modifier.padding(14.dp)) {
            Text(title)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
