package mx.edu.utez.fitlife.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import mx.edu.utez.fitlife.ui.theme.LightBlue

const val DEFAULT_DAILY_GOAL = 6000

@Composable
fun DailyGoal(
    currentSteps: Int = 0,
    goalSteps: Int = DEFAULT_DAILY_GOAL
) {
    val progress = (currentSteps / goalSteps.toFloat()).coerceIn(0f, 1f)
    val stepsRemaining = (goalSteps - currentSteps).coerceAtLeast(0)

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

        Text(
            if (stepsRemaining > 0) {
                "${stepsRemaining} pasos restantes"
            } else {
                "¡Meta alcanzada!"
            },
            color = Color.White
        )
    }
}
