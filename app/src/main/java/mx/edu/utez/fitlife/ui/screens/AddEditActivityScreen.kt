package mx.edu.utez.fitlife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import mx.edu.utez.fitlife.data.model.ActivityDay
import mx.edu.utez.fitlife.ui.components.Header
import mx.edu.utez.fitlife.ui.components.buttons.PrimaryButton
import mx.edu.utez.fitlife.ui.components.inputs.TextInput
import mx.edu.utez.fitlife.ui.theme.PrimaryBlue
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

    // Prellenar cuando la actividad exista y se cargue
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

    Column {
        // Header personalizado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
            Text(
                title,
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balancear el espacio
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostrar error global si existe
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

            // Campo: Día
            Column {
                Text(
                    "Día de la semana",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = day,
                    onValueChange = {
                        day = it
                        dayError = null
                    },
                    placeholder = { Text("Ej: Lun, Mar, Mié...") },
                    label = { Text("Día") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = dayError != null,
                    supportingText = dayError?.let { { Text(it) } }
                )
            }

            // Campo: Pasos
            Column {
                Text(
                    "Pasos",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = steps,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            steps = it
                            stepsError = null
                        }
                    },
                    placeholder = { Text("Ej: 5000") },
                    label = { Text("Número de pasos") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = stepsError != null,
                    supportingText = stepsError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            // Campo: Distancia
            Column {
                Text(
                    "Distancia (km)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = distanceKm,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d+(\\.\\d*)?$"))) {
                            distanceKm = it
                            distanceError = null
                        }
                    },
                    placeholder = { Text("Ej: 3.5") },
                    label = { Text("Distancia en kilómetros") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = distanceError != null,
                    supportingText = distanceError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }

            // Campo: Tiempo activo
            Column {
                Text(
                    "Tiempo activo",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = activeTime,
                    onValueChange = {
                        activeTime = it
                        timeError = null
                    },
                    placeholder = { Text("Ej: 45m o 1h 30m") },
                    label = { Text("Tiempo activo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = timeError != null,
                    supportingText = timeError?.let { { Text(it) } }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            PrimaryButton(
                text = if (isEditMode) "Actualizar Actividad" else "Crear Actividad",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    // Validar campos
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
                }
            )

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
        }
    }
}

