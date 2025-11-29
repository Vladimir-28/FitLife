package mx.edu.utez.fitlife.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

import mx.edu.utez.fitlife.ui.components.*
import mx.edu.utez.fitlife.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {

    Column {

        Header("Mi Perfil")

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                Modifier
                    .size(90.dp)
                    .background(PrimaryBlue, CircleShape)
            )

            Text("María González", style = MaterialTheme.typography.titleLarge)
            Text("maria.gonzalez@email.com")

            Divider()

            Text("Información personal")

            Card {
                Column(Modifier.padding(12.dp)) {
                    Text("Nombre completo")
                    Text("María González")
                }
            }

            Text("Datos físicos")

            Row {
                InfoChip("Altura","165cm", BlueSoft,BlueAccent)
                InfoChip("Peso","62kg", RedSoft,RedAccent)
            }

            Text("Estadísticas")

            Row {
                InfoChip("Tiempo","42h", BlueSoft,BlueAccent)
                InfoChip("KM","15.2", RedSoft,RedAccent)
            }

            InfoChip("Pasos","34,567",OrangeSoft,OrangeAccent)
        }

        Spacer(Modifier.weight(1f))
        BottomPillBar(navController, "profile")
    }
}
