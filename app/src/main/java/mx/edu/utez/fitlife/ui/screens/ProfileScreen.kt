package mx.edu.utez.fitlife.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import mx.edu.utez.fitlife.ui.components.*
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.AuthViewModel
import mx.edu.utez.fitlife.viewmodel.ActivityViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(context.applicationContext as Application) as T
            }
        }
    )
    
    val activityViewModel: ActivityViewModel = viewModel()
    val activities by activityViewModel.activities.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Verificar sesión al cargar
    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }
    
    // Calcular estadísticas reales desde las actividades
    val totalSteps = activities.sumOf { it.steps }
    val totalDistance = activities.sumOf { it.distanceKm.toDouble() }.toFloat()
    val totalTimeMinutes = activities.sumOf { 
        parseTimeToMinutes(it.activeTime) 
    }
    
    // Formatear tiempo total
    val totalTimeFormatted = formatMinutesToTime(totalTimeMinutes)

    Column {

        Header("Mi Perfil")

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                Modifier
                    .size(90.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    currentUser?.name?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }

            Text(
                currentUser?.name ?: "Usuario",
                style = MaterialTheme.typography.titleLarge
            )
            Text(currentUser?.email ?: "email@ejemplo.com")

            Divider()

            Text(
                "Información personal",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "Nombre completo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        currentUser?.name ?: "No disponible",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Email",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        currentUser?.email ?: "No disponible",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Text(
                "Estadísticas de Actividad",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    "Tiempo total",
                    totalTimeFormatted,
                    BlueSoft,
                    BlueAccent
                )
                InfoChip(
                    "Distancia total",
                    "${String.format("%.1f", totalDistance)} km",
                    RedSoft,
                    RedAccent
                )
            }

            InfoChip(
                "Pasos totales",
                totalSteps.toString(),
                OrangeSoft,
                OrangeAccent
            )
            
            if (activities.isNotEmpty()) {
                Text(
                    "Actividades registradas: ${activities.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "No hay actividades registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de cerrar sesión
            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }

        Spacer(Modifier.weight(1f))
        BottomPillBar(navController, "profile")
    }
}
