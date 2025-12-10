package mx.edu.utez.fitlife.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.utez.fitlife.data.model.ActivityDay
import mx.edu.utez.fitlife.ui.components.*
import mx.edu.utez.fitlife.ui.components.cards.DailyGoal
import mx.edu.utez.fitlife.ui.components.cards.DEFAULT_DAILY_GOAL
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.ActivityViewModel
import androidx.activity.ComponentActivity

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ActivityViewModel = viewModel(LocalContext.current as ComponentActivity)
    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Estado para el diálogo de confirmación de eliminación
    var showDeleteDialog by remember { mutableStateOf<ActivityDay?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
        Header("FitTracker")

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mostrar error si existe
            error?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Mostrar loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Calcular totales desde los datos reales
            val totalSteps = activities.sumOf { it.steps }
            val totalDistance = activities.sumOf { it.distanceKm.toDouble() }.toFloat()
            val totalTimeMinutes = activities.sumOf { 
                parseTimeToMinutes(it.activeTime) 
            }

    val goalSteps = DEFAULT_DAILY_GOAL
    // Calcular progreso diario (basado en meta configurable)
            val todaySteps = if (activities.isNotEmpty()) {
                activities.lastOrNull()?.steps ?: 0
            } else {
                0
            }
    DailyGoal(currentSteps = todaySteps, goalSteps = goalSteps)

            // Tarjeta del sensor de pasos
            SensorCard(viewModel)

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    "Distancia",
                    "${String.format("%.1f", totalDistance)} km",
                    RedSoft,
                    RedAccent
                )

                InfoChip(
                    "Tiempo activo",
                    formatMinutesToTime(totalTimeMinutes),
                    BlueSoft,
                    BlueAccent
                )
            }

            InfoChip(
                "Pasos",
                totalSteps.toString(),
                OrangeSoft,
                OrangeAccent
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Weekly Progress",
                style = MaterialTheme.typography.titleMedium
            )

            // Mostrar lista de actividades
            if (activities.isNotEmpty()) {
                activities.forEach { activity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(CardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    activity.day,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Botón editar
                                    IconButton(
                                        onClick = {
                                            activity.id?.let { id ->
                                                navController.navigate("edit_activity/$id")
                                            }
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    // Botón eliminar
                                    IconButton(
                                        onClick = {
                                            showDeleteDialog = activity
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "${activity.steps} pasos",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "${activity.distanceKm} km",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    activity.activeTime,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            } else if (!isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = CardDefaults.cardColors(CardBg)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay actividades registradas",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

            Spacer(Modifier.weight(1f))
            BottomPillBar(navController, "home")
        }

        // Floating Action Button para crear nueva actividad
        FloatingActionButton(
            onClick = {
                navController.navigate("add_activity")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PrimaryBlue
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar actividad",
                tint = Color.White
            )
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { activityToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Actividad") },
            text = { 
                Text("¿Estás seguro de que deseas eliminar la actividad del ${activityToDelete.day}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activityToDelete.id?.let { id ->
                            viewModel.deleteActivity(id)
                        }
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Convierte un string de tiempo (ej: "1h 30m" o "45m") a minutos
 */
fun parseTimeToMinutes(time: String): Int {
    return try {
        val trimmed = time.trim()
        // Formato "Xh Ym"
        if (trimmed.contains("h")) {
            val parts = trimmed.split("h")
            val hours = parts[0].trim().toIntOrNull() ?: 0
            val minutes = parts.getOrNull(1)
                ?.replace("m", "", ignoreCase = true)
                ?.trim()
                ?.toIntOrNull() ?: 0
            hours * 60 + minutes
        } else if (trimmed.contains("m")) {
            trimmed.replace("m", "", ignoreCase = true)
                .trim()
                .toIntOrNull() ?: 0
        } else if (trimmed.contains("s")) {
            // Segundos: redondear hacia arriba a 1 minuto si hay al menos 30s
            val seconds = trimmed.replace("s", "", ignoreCase = true)
                .trim()
                .toIntOrNull() ?: 0
            if (seconds >= 30) 1 else 0
        } else {
            trimmed.toIntOrNull() ?: 0
        }
    } catch (e: Exception) {
        0
    }
}

/**
 * Convierte minutos a formato legible (ej: "2h 15m" o "45m")
 */
fun formatMinutesToTime(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}
