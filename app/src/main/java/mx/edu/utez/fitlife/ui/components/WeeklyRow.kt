package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utez.fitlife.data.model.ActivityDay

@Composable
fun WeeklyRow(day: ActivityDay) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(day.day, style = MaterialTheme.typography.titleMedium)

            Column {
                Text("👣 ${day.steps} pasos")
                Text("📏 ${day.distanceKm} km")
                Text("⏱ ${day.activeTime}")
            }
        }
    }
}
