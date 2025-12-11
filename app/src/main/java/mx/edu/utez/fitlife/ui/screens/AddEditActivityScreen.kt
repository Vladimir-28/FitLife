package mx.edu.utez.fitlife.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.activity.ComponentActivity
import mx.edu.utez.fitlife.data.model.ActivityDay
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.ActivityViewModel

@Composable
fun AddEditActivityScreen(
    navController: NavController,
    activityId: Int? = null
) {
    val viewModel: ActivityViewModel = viewModel(LocalContext.current as ComponentActivity)
    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(activityId) {
        if (activityId != null && activities.isEmpty()) {
            viewModel.loadActivities()
        }
    }

    // Estados del formulario
    var day by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var distanceKm by remember { mutableStateOf("") }
    var activeTime by remember { mutableStateOf("") }

    // Prellenar cuando la actividad exista
    val existingActivity = activities.firstOrNull { it.id == activityId }
    LaunchedEffect(existingActivity?.id) {
        existingActivity?.let {
            day = it.day
            steps = it.steps.toString()
            distanceKm = it.distanceKm.toString()
            activeTime = it.activeTime
        }
    }

    // Estados de validación
    var dayError by remember { mutableStateOf<String?>(null) }
    var stepsError by remember { mutableStateOf<String?>(null) }
    var distanceError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    val isEditMode = activityId != null
    val title = if (isEditMode) "Editar Actividad" else "Nueva Actividad"
    
    var submitAttempted by remember { mutableStateOf(false) }
    LaunchedEffect(submitAttempted, isLoading, error) {
        if (submitAttempted && !isLoading && error == null) {
            navController.popBackStack()
            submitAttempted = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isEditMode) {
                            listOf(AccentOrange, Color(0xFFEA580C))
                        } else {
                            listOf(SecondaryGreen, SecondaryGreenDark)
                        }
                    )
                )
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error global
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

            // Icono descriptivo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isEditMode) AccentOrange.copy(alpha = 0.1f)
                            else SecondaryGreen.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Default.Edit else Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = if (isEditMode) AccentOrange else SecondaryGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            // Campo: Día
            OutlinedTextField(
                value = day,
                onValueChange = {
                    day = it
                    dayError = null
                },
                label = { Text("Día de la semana") },
                placeholder = { Text("Ej: Lunes, Martes...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = if (dayError != null) Error else TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = dayError != null,
                supportingText = dayError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Campo: Pasos
            OutlinedTextField(
                value = steps,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        steps = it
                        stepsError = null
                        
                        // Auto-calcular distancia
                        it.toIntOrNull()?.let { stepsInt ->
                            val calculatedDistance = stepsInt * 0.00075f
                            distanceKm = String.format("%.2f", calculatedDistance)
                        }
                    }
                },
                label = { Text("Número de pasos") },
                placeholder = { Text("Ej: 5000") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DirectionsWalk,
                        contentDescription = null,
                        tint = if (stepsError != null) Error else TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = stepsError != null,
                supportingText = stepsError?.let { { Text(it) } } ?: {
                    Text("La distancia se calculará automáticamente")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Campo: Distancia
            OutlinedTextField(
                value = distanceKm,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d+(\\.\\d*)?$"))) {
                        distanceKm = it
                        distanceError = null
                    }
                },
                label = { Text("Distancia (km)") },
                placeholder = { Text("Ej: 3.5") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Route,
                        contentDescription = null,
                        tint = if (distanceError != null) Error else TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = distanceError != null,
                supportingText = distanceError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Campo: Tiempo activo
            OutlinedTextField(
                value = activeTime,
                onValueChange = {
                    activeTime = it
                    timeError = null
                },
                label = { Text("Tiempo activo") },
                placeholder = { Text("Ej: 45m o 1h 30m") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = if (timeError != null) Error else TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = timeError != null,
                supportingText = timeError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            Button(
                onClick = {
                    var hasError = false

                    if (day.isBlank()) {
                        dayError = "El día es requerido"
                        hasError = true
                    }

                    if (steps.isBlank()) {
                        stepsError = "Los pasos son requeridos"
                        hasError = true
                    } else {
                        val stepsInt = steps.toIntOrNull()
                        if (stepsInt == null || stepsInt < 0) {
                            stepsError = "Ingrese un número válido"
                            hasError = true
                        }
                    }

                    if (distanceKm.isBlank()) {
                        distanceError = "La distancia es requerida"
                        hasError = true
                    } else {
                        val distanceFloat = distanceKm.toFloatOrNull()
                        if (distanceFloat == null || distanceFloat < 0) {
                            distanceError = "Ingrese un número válido"
                            hasError = true
                        }
                    }

                    if (activeTime.isBlank()) {
                        timeError = "El tiempo activo es requerido"
                        hasError = true
                    }

                    if (!hasError) {
                        val activity = ActivityDay(
                            id = if (isEditMode) activityId else null,
                            day = day.trim(),
                            steps = steps.toInt(),
                            distanceKm = distanceKm.toFloat(),
                            activeTime = activeTime.trim()
                        )

                        submitAttempted = true
                        if (isEditMode && activityId != null) {
                            viewModel.updateActivity(activityId, activity)
                        } else {
                            viewModel.createActivity(activity)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditMode) AccentOrange else SecondaryGreen
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isEditMode) Icons.Default.Save else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Actualizar Actividad" else "Crear Actividad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Botón cancelar
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                )
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
