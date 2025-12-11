package mx.edu.utez.fitlife.ui.components

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import mx.edu.utez.fitlife.data.model.ActivityDay
import mx.edu.utez.fitlife.service.StepCounterService
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SensorCard(viewModel: ActivityViewModel) {
    val context = LocalContext.current
    
    // Estados del servicio
    var stepService by remember { mutableStateOf<StepCounterService?>(null) }
    var isBound by remember { mutableStateOf(false) }
    
    // Estados observables del servicio
    val stepCount by stepService?.stepCount?.collectAsState() ?: remember { mutableStateOf(0) }.let { it }
    val distance by stepService?.distance?.collectAsState() ?: remember { mutableStateOf(0.0) }.let { it }
    val elapsedTime by stepService?.elapsedTime?.collectAsState() ?: remember { mutableStateOf(0L) }.let { it }
    val isRunning by stepService?.isRunning?.collectAsState() ?: remember { mutableStateOf(false) }.let { it }
    val isSensorAvailable by stepService?.isSensorAvailable?.collectAsState() ?: remember { mutableStateOf(true) }.let { it }
    
    // Permisos
    var hasActivityPermission by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasNotificationPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // Service Connection
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as StepCounterService.LocalBinder
                stepService = binder.getService()
                isBound = true
            }
            
            override fun onServiceDisconnected(name: ComponentName?) {
                stepService = null
                isBound = false
            }
        }
    }
    
    // Permission launchers
    val activityPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasActivityPermission = isGranted
    }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
    }
    
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }
    
    // Check permissions on start
    LaunchedEffect(Unit) {
        hasActivityPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
        
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
        
        // Bind to service
        Intent(context, StepCounterService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            if (isBound) {
                context.unbindService(serviceConnection)
            }
        }
    }
    
    // Animación de pulso cuando está activo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isRunning) SecondaryGreen.copy(alpha = 0.3f)
                           else PrimaryBlue.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isRunning) {
                            listOf(SecondaryGreen, SecondaryGreenDark)
                        } else {
                            listOf(AccentPurple, Color(0xFF7C3AED))
                        }
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Indicador de estado con animación
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isRunning) Color.White.copy(alpha = pulseAlpha)
                                    else Color.White.copy(alpha = 0.5f)
                                )
                        )
                        
                        Column {
                            Text(
                                text = if (isRunning) "Rastreando actividad" else "Sensor de Pasos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isRunning) "En segundo plano" else "Toca para iniciar",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    
                    // Sensor status
                    if (isSensorAvailable) {
                        Icon(
                            imageVector = Icons.Outlined.Sensors,
                            contentDescription = "Sensor disponible",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Pasos
                    StatItem(
                        icon = Icons.Outlined.DirectionsWalk,
                        value = stepCount.toString(),
                        label = "Pasos"
                    )
                    
                    // Distancia
                    StatItem(
                        icon = Icons.Outlined.Route,
                        value = String.format("%.2f", distance),
                        label = "km"
                    )
                    
                    // Tiempo
                    StatItem(
                        icon = Icons.Outlined.Timer,
                        value = formatElapsedTime(elapsedTime),
                        label = "Tiempo"
                    )
                }
                
                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón principal (Iniciar/Detener)
                    Button(
                        onClick = {
                            if (!hasActivityPermission || !hasNotificationPermission) {
                                showPermissionDialog = true
                                return@Button
                            }
                            
                            if (isRunning) {
                                stepService?.stopTracking()
                            } else {
                                // Request location permission if not granted
                                if (!hasLocationPermission) {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                                
                                // Start service
                                Intent(context, StepCounterService::class.java).apply {
                                    action = StepCounterService.ACTION_START
                                }.also { intent ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(intent)
                                    } else {
                                        context.startService(intent)
                                    }
                                }
                                
                                stepService?.startTracking()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = if (isRunning) Error else SecondaryGreen
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRunning) "Detener" else "Iniciar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Botón guardar (solo visible cuando hay datos)
                    AnimatedVisibility(
                        visible = stepCount > 0 && !isRunning,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        Button(
                            onClick = {
                                val currentDay = SimpleDateFormat("EEE", Locale.getDefault())
                                    .format(Date()).substring(0, 3)
                                
                                val activity = ActivityDay(
                                    day = currentDay,
                                    steps = stepCount,
                                    distanceKm = distance.toFloat(),
                                    activeTime = formatElapsedTime(elapsedTime)
                                )
                                
                                viewModel.createActivity(activity)
                                stepService?.resetTracking()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guardar",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Botón reset (solo visible cuando hay datos y está pausado)
                AnimatedVisibility(
                    visible = (stepCount > 0 || elapsedTime > 0) && !isRunning,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    TextButton(
                        onClick = {
                            stepService?.resetTracking()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Reiniciar contador",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
            },
            title = {
                Text("Permisos necesarios")
            },
            text = {
                Text(
                    "Para rastrear tu actividad en segundo plano, necesitamos los siguientes permisos:\n\n" +
                    "• Reconocimiento de actividad (para contar pasos)\n" +
                    "• Notificaciones (para mostrar progreso)\n" +
                    "• Ubicación (opcional, para mejor precisión de distancia)"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        
                        // Request permissions
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasActivityPermission) {
                            activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        }
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                ) {
                    Text("Conceder permisos")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

fun formatElapsedTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    
    return when {
        hours > 0 -> String.format("%dh %02dm", hours, minutes)
        minutes > 0 -> String.format("%dm %02ds", minutes, seconds)
        else -> String.format("%ds", seconds)
    }
}
