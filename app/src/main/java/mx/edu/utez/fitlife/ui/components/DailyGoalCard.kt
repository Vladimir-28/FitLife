package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import mx.edu.utez.fitlife.ui.theme.LightBlue

@Composable
fun DailyGoal(progress: Float) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                LightBlue,
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Daily Goal", color = Color.White)
            Text("${(progress * 100).toInt()}%", color = Color.White)

        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )

        Spacer(Modifier.height(6.dp))

        Text("1,751 steps to go", color = Color.White)
    }
}
