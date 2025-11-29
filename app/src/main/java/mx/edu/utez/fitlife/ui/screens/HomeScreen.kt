package mx.edu.utez.fitlife.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import mx.edu.utez.fitlife.ui.components.*
import mx.edu.utez.fitlife.ui.components.cards.DailyGoal
import mx.edu.utez.fitlife.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {

    Column {

        Header("FitTracker")

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            DailyGoal(0.82f)

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    "Distancia",
                    "2h 15m",
                    RedSoft,
                    RedAccent
                )

                InfoChip(
                    "Tiempo activo",
                    "2h 15m",
                    BlueSoft,
                    BlueAccent
                )
            }

            InfoChip(
                "Pasos",
                "1242",
                OrangeSoft,
                OrangeAccent
            )

            Spacer(Modifier.height(8.dp))

            Text("Weekly Progress")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = CardDefaults.cardColors(CardBg)
            ) {}

        }

        Spacer(Modifier.weight(1f))
        BottomPillBar(navController, "home")
    }
}
