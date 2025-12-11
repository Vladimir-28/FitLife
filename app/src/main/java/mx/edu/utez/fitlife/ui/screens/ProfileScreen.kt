package mx.edu.utez.fitlife.ui.screens

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.utez.fitlife.ui.components.*
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.AuthViewModel
import mx.edu.utez.fitlife.viewmodel.ActivityViewModel
import androidx.activity.ComponentActivity
import androidx.compose.material3.SegmentedButtonDefaults.Icon

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
    
    val activityViewModel: ActivityViewModel = viewModel(context as ComponentActivity)
    val activities by activityViewModel.activities.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }
    
    // Estadísticas
    val totalSteps = activities.sumOf { it.steps }
    val totalDistance = activities.sumOf { it.distanceKm.toDouble() }.toFloat()
    val totalTimeMinutes = activities.sumOf { parseTimeToMinutes(it.activeTime) }
    val totalCalories = (totalSteps * 0.04).toInt()
    val avgStepsPerDay = if (activities.isNotEmpty()) totalSteps / activities.size else 0

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header con perfil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryBlue,
                            PrimaryBlueDark,
                            AccentPurple
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentUser?.name ?: "Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = currentUser?.email ?: "email@ejemplo.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickStat(
                        value = activities.size.toString(),
                        label = "Actividades"
                    )
                    QuickStat(
                        value = "${avgStepsPerDay}",
                        label = "Promedio/día"
                    )
                    QuickStat(
                        value = "$totalCalories",
                        label = "Calorías"
                    )
                }
            }
        }

        // Contenido
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .offset(y = (-16).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estadísticas detalladas
            Text(
                text = "Estadísticas Totales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Outlined.DirectionsWalk,
                    value = totalSteps.toString(),
                    label = "Pasos totales",
                    color = AccentOrange,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Outlined.Route,
                    value = "${String.format("%.1f", totalDistance)} km",
                    label = "Distancia",
                    color = Error,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Outlined.Timer,
                    value = formatMinutesToTime(totalTimeMinutes),
                    label = "Tiempo activo",
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Outlined.LocalFireDepartment,
                    value = "$totalCalories cal",
                    label = "Quemadas",
                    color = SecondaryGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sección de cuenta
            Text(
                text = "Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Outlined.Person,
                        title = "Información personal",
                        subtitle = currentUser?.name ?: "No disponible",
                        onClick = { /* TODO: Editar perfil */ }
                    )
                    
                    HorizontalDivider(color = DividerColor)
                    
                    ProfileMenuItem(
                        icon = Icons.Outlined.Email,
                        title = "Correo electrónico",
                        subtitle = currentUser?.email ?: "No disponible",
                        onClick = { }
                    )
                    
                    HorizontalDivider(color = DividerColor)
                    
                    ProfileMenuItem(
                        icon = Icons.Outlined.FitnessCenter,
                        title = "Meta diaria",
                        subtitle = "6,000 pasos",
                        onClick = { /* TODO: Configurar meta */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón de cerrar sesión
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error.copy(alpha = 0.1f),
                    contentColor = Error
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Espaciado para el bottom bar
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Navigation
        BottomPillBar(navController, "profile")
    }
    
    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Cerrar Sesión",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro de que deseas cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun QuickStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ProfileStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = color.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary
            )
        }
    }
}
