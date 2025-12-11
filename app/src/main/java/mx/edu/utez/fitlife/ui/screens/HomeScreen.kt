package mx.edu.utez.fitlife.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ActivityViewModel = viewModel(LocalContext.current as ComponentActivity)
    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf<ActivityDay?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header mejorado
            Header(
                title = "FitLife",
                subtitle = "¡Mantente activo hoy!"
            )

            // Contenido scrolleable
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error Card
                item {
                    AnimatedVisibility(
                        visible = error != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Error.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Error
                                )
                                Text(
                                    text = error ?: "",
                                    color = Error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Loading indicator
                item {
                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                }

                // Meta diaria
                item {
                    val todayKey = SimpleDateFormat("EEE", Locale.getDefault())
                        .format(Date()).take(3).lowercase()
                    val todaySteps = activities
                        .filter { it.day.take(3).lowercase() == todayKey }
                        .sumOf { it.steps }
                    
                    DailyGoal(
                        currentSteps = todaySteps,
                        goalSteps = DEFAULT_DAILY_GOAL
                    )
                }

                // Sensor Card
                item {
                    SensorCard(viewModel)
                }

                // Stats Cards
                item {
                    val totalSteps = activities.sumOf { it.steps }
                    val totalDistance = activities.sumOf { it.distanceKm.toDouble() }.toFloat()
                    val totalTimeMinutes = activities.sumOf { parseTimeToMinutes(it.activeTime) }

                    Text(
                        text = "Resumen Total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "Pasos",
                            value = totalSteps.toString(),
                            icon = Icons.Outlined.DirectionsWalk,
                            color = AccentOrange,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Distancia",
                            value = "${String.format("%.2f", totalDistance)} km",
                            icon = Icons.Outlined.Route,
                            color = Error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatCard(
                        label = "Tiempo activo",
                        value = formatMinutesToTime(totalTimeMinutes),
                        icon = Icons.Outlined.Timer,
                        color = PrimaryBlue,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Sección de actividades
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Actividades Recientes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activities.size} registros",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // Lista de actividades
                if (activities.isNotEmpty()) {
                    items(
                        items = activities,
                        key = { it.id ?: it.hashCode() }
                    ) { activity ->
                        ActivityCard(
                            activity = activity,
                            onEdit = {
                                activity.id?.let { id ->
                                    navController.navigate("edit_activity/$id")
                                }
                            },
                            onDelete = {
                                showDeleteDialog = activity
                            }
                        )
                    }
                } else if (!isLoading) {
                    item {
                        EmptyStateCard()
                    }
                }

                // Espacio para el FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Bottom Navigation
            BottomPillBar(navController, "home")
        }

        // FAB
        FloatingActionButton(
            onClick = { navController.navigate("add_activity") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar actividad",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Delete Dialog
    showDeleteDialog?.let { activityToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Eliminar Actividad",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar la actividad del ${activityToDelete.day}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        activityToDelete.id?.let { id ->
                            viewModel.deleteActivity(id)
                        }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ActivityCard(
    activity: ActivityDay,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = PrimaryBlue.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, AccentPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activity.day.take(3).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Column {
                        Text(
                            text = activity.day,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${activity.steps} pasos",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = DividerColor
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActivityStat(
                    icon = Icons.Outlined.Route,
                    value = "${activity.distanceKm} km",
                    color = Error
                )
                ActivityStat(
                    icon = Icons.Outlined.Timer,
                    value = activity.activeTime,
                    color = PrimaryBlue
                )
                ActivityStat(
                    icon = Icons.Outlined.LocalFireDepartment,
                    value = "${(activity.steps * 0.04).toInt()} cal",
                    color = AccentOrange
                )
            }
        }
    }
}

@Composable
private fun ActivityStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun EmptyStateCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.DirectionsRun,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Sin actividades",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                text = "Usa el sensor de pasos o agrega una actividad manualmente",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

fun parseTimeToMinutes(time: String): Int {
    return try {
        val trimmed = time.trim()
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

fun formatMinutesToTime(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}
