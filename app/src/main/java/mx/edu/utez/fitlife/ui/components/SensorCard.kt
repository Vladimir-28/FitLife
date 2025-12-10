package mx.edu.utez.fitlife.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
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
    var hasPermission by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    
    // Verificar permiso al iniciar
    val hasActivityRecognitionPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No se requiere permiso en versiones anteriores
        }
    }
    
    // Launcher para solicitar permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            showPermissionRationale = false
        } else {
            showPermissionRationale = true
        }
    }
    
    // Inicializar estado de permiso
    LaunchedEffect(Unit) {
        hasPermission = hasActivityRecognitionPermission
    }
    
    // Actualizar tiempo transcurrido cada segundo solo cuando está escuchando
    LaunchedEffect(isListening.value) {
        if (isListening.value && startTime != null) {
            while (isListening.value) {
                kotlinx.coroutines.delay(1000)
                startTime?.let {
                    elapsedTime = System.currentTimeMillis() - it
                }
            }
        }
    }
    
    // Limpiar al desmontar
    DisposableEffect(Unit) {
        onDispose {
            sensorManager.stopListening()
        }
    }
    
    // Función para verificar y solicitar permiso antes de iniciar
    fun requestPermissionIfNeeded(onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (hasPermission) {
                onPermissionGranted()
            } else {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            // No se requiere permiso en versiones anteriores a Android 10
            onPermissionGranted()
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
                                // Pausar: detener sensor y tiempo
                                sensorManager.stopListening()
                                isListening.value = false
                            } else {
                                // Verificar permiso antes de iniciar
                                requestPermissionIfNeeded {
                                    // Iniciar: resetear contador, iniciar sensor y tiempo
                                    sensorManager.resetStepCount()  // Resetear pasos antes de iniciar
                                    startTime = System.currentTimeMillis()  // Iniciar tiempo
                                    elapsedTime = 0L  // Resetear tiempo transcurrido
                                    sensorManager.startListening()  // Iniciar sensor
                                    isListening.value = true
                                    showPermissionRationale = false
                                }
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
            
            // Mostrar mensaje si no hay permiso
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasPermission && showPermissionRationale) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Permiso requerido",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            "Se necesita el permiso de reconocimiento de actividad para detectar pasos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Button(
                            onClick = {
                                requestPermissionIfNeeded {
                                    hasPermission = true
                                    showPermissionRationale = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Conceder permiso")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (isSensorAvailable && (hasPermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)) {
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
                            startTime = null
                            elapsedTime = 0L
                            isListening.value = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = stepCount > 0 && !isListening.value,  // Solo habilitado cuando está pausado
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
                
                if (stepCount > 0 || elapsedTime > 0) {
                    TextButton(
                        onClick = {
                            // Solo reiniciar si está pausado
                            if (!isListening.value) {
                                sensorManager.resetStepCount()
                                elapsedTime = 0L
                                startTime = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isListening.value  // Solo habilitado cuando está pausado
                    ) {
                        Text(
                            "Reiniciar contador",
                            color = if (!isListening.value) 
                                Color.White.copy(alpha = 0.8f) 
                            else 
                                Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            } else if (!isSensorAvailable) {
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

