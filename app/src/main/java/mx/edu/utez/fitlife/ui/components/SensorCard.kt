package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mx.edu.utez.fitlife.data.model.ActivityDay
import mx.edu.utez.fitlife.data.sensor.StepSensorManager
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SensorCard(viewModel: ActivityViewModel) {
    val context = LocalContext.current
    val sensorManager = remember { StepSensorManager(context) }
    
    val stepCount by sensorManager.stepCount.collectAsState()
    val isSensorAvailable by sensorManager.isSensorAvailable.collectAsState()
    val isListening = remember { mutableStateOf(false) }
    
    var startTime by remember { mutableStateOf<Long?>(null) }
    var elapsedTime by remember { mutableStateOf(0L) }
    
    // Actualizar tiempo transcurrido cada segundo
    LaunchedEffect(isListening.value) {
        if (isListening.value) {
            while (isListening.value) {
                kotlinx.coroutines.delay(1000)
                startTime?.let {
                    elapsedTime = System.currentTimeMillis() - it
                }
            }
        }
    }
    
    // Inicializar sensor al montar
    LaunchedEffect(Unit) {
        sensorManager.startListening()
        isListening.value = true
    }
    
    // Limpiar al desmontar
    DisposableEffect(Unit) {
        onDispose {
            sensorManager.stopListening()
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightBlue
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Sensor de Pasos",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSensorAvailable) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Sensor disponible",
                            tint = Color.Green,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Sensor no disponible",
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            if (isListening.value) {
                                sensorManager.stopListening()
                                isListening.value = false
                            } else {
                                sensorManager.startListening()
                                isListening.value = true
                                startTime = System.currentTimeMillis()
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            if (isListening.value) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = if (isListening.value) "Pausar" else "Iniciar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            if (isSensorAvailable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Pasos detectados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            stepCount.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Tiempo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            formatElapsedTime(elapsedTime),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }
                
                // Calcular distancia aproximada (promedio: 0.7m por paso = 0.0007 km)
                val distanceKm = (stepCount * 0.0007f)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        if (stepCount > 0) {
                            val currentDay = SimpleDateFormat("EEE", Locale.getDefault())
                                .format(Date()).substring(0, 3)
                            
                            val activity = ActivityDay(
                                day = currentDay,
                                steps = stepCount,
                                distanceKm = distanceKm,
                                activeTime = formatElapsedTime(elapsedTime)
                            )
                            
                            viewModel.createActivity(activity)
                            
                            // Resetear contador después de guardar
                            sensorManager.resetStepCount()
                            startTime = System.currentTimeMillis()
                            elapsedTime = 0L
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = stepCount > 0 && isListening.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryBlue
                    )
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Actividad")
                }
                
                if (stepCount > 0) {
                    TextButton(
                        onClick = {
                            sensorManager.resetStepCount()
                            startTime = System.currentTimeMillis()
                            elapsedTime = 0L
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Reiniciar contador",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                Text(
                    "El sensor de pasos no está disponible en este dispositivo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun formatElapsedTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else if (minutes > 0) {
        "${minutes}m ${seconds}s"
    } else {
        "${seconds}s"
    }
}

