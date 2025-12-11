package mx.edu.utez.fitlife.ui.screens

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.AuthViewModel

enum class ForgotPasswordStep {
    EMAIL,
    TOKEN,
    NEW_PASSWORD,
    SUCCESS
}

@Composable
fun ForgotPasswordScreen(nav: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(context.applicationContext as Application) as T
            }
        }
    )
    
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorState by authViewModel.error.collectAsState()
    val resetToken by authViewModel.resetToken.collectAsState()
    
    var currentStep by remember { mutableStateOf(ForgotPasswordStep.EMAIL) }
    
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var tokenError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    // Limpiar estado al salir
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clearResetState()
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
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AccentOrange,
                            Color(0xFFEA580C),
                            PrimaryBlueDark
                        )
                    )
                )
        ) {
            // Botón de regreso
            IconButton(
                onClick = { 
                    if (currentStep == ForgotPasswordStep.EMAIL) {
                        nav.popBackStack()
                    } else {
                        currentStep = ForgotPasswordStep.EMAIL
                        authViewModel.clearResetState()
                        authViewModel.clearError()
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (currentStep) {
                            ForgotPasswordStep.EMAIL -> Icons.Default.Email
                            ForgotPasswordStep.TOKEN -> Icons.Default.Pin
                            ForgotPasswordStep.NEW_PASSWORD -> Icons.Default.LockReset
                            ForgotPasswordStep.SUCCESS -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = when (currentStep) {
                        ForgotPasswordStep.EMAIL -> "Recuperar Contraseña"
                        ForgotPasswordStep.TOKEN -> "Verificar Código"
                        ForgotPasswordStep.NEW_PASSWORD -> "Nueva Contraseña"
                        ForgotPasswordStep.SUCCESS -> "¡Listo!"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { index ->
                    val stepIndex = ForgotPasswordStep.entries[index]
                    Box(
                        modifier = Modifier
                            .size(if (currentStep == stepIndex) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentStep.ordinal >= index) AccentOrange
                                else TextTertiary
                            )
                    )
                    if (index < 3) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            when (currentStep) {
                ForgotPasswordStep.EMAIL -> {
                    Text(
                        text = "Ingresa tu correo electrónico y te enviaremos un código para restablecer tu contraseña.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            emailError = null
                            authViewModel.clearError()
                        },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("ejemplo@correo.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }
                
                ForgotPasswordStep.TOKEN -> {
                    Text(
                        text = "Ingresa el código que recibiste en tu correo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Mostrar token para desarrollo
                    resetToken?.let { devToken ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Info.copy(alpha = 0.1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "🔧 Modo desarrollo:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Info
                                )
                                Text(
                                    text = devToken,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Info
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = token,
                        onValueChange = { 
                            token = it
                            tokenError = null
                            authViewModel.clearError()
                        },
                        label = { Text("Código de verificación") },
                        placeholder = { Text("Ingresa el código") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Pin,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = tokenError != null,
                        supportingText = tokenError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }
                
                ForgotPasswordStep.NEW_PASSWORD -> {
                    Text(
                        text = "Crea una nueva contraseña segura para tu cuenta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { 
                            newPassword = it
                            passwordError = null
                            authViewModel.clearError()
                        },
                        label = { Text("Nueva contraseña") },
                        placeholder = { Text("Mínimo 6 caracteres") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                                                 else Icons.Outlined.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordError != null,
                        supportingText = passwordError?.let { { Text(it) } },
                        visualTransformation = if (passwordVisible) VisualTransformation.None 
                                               else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            confirmError = null
                            authViewModel.clearError()
                        },
                        label = { Text("Confirmar contraseña") },
                        placeholder = { Text("Repite tu contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.LockReset,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmError != null,
                        supportingText = confirmError?.let { { Text(it) } },
                        visualTransformation = if (passwordVisible) VisualTransformation.None 
                                               else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }
                
                ForgotPasswordStep.SUCCESS -> {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SecondaryGreen,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Tu contraseña ha sido actualizada correctamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = "Ya puedes iniciar sesión con tu nueva contraseña.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Error message
            AnimatedVisibility(
                visible = errorState != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Error.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = errorState ?: "",
                            color = Error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de acción
            Button(
                onClick = {
                    when (currentStep) {
                        ForgotPasswordStep.EMAIL -> {
                            if (email.isBlank()) {
                                emailError = "El correo es requerido"
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailError = "Correo inválido"
                            } else {
                                authViewModel.forgotPassword(email.trim().lowercase()) {
                                    currentStep = ForgotPasswordStep.TOKEN
                                }
                            }
                        }
                        ForgotPasswordStep.TOKEN -> {
                            if (token.isBlank()) {
                                tokenError = "El código es requerido"
                            } else {
                                authViewModel.verifyResetToken(token.trim()) {
                                    currentStep = ForgotPasswordStep.NEW_PASSWORD
                                }
                            }
                        }
                        ForgotPasswordStep.NEW_PASSWORD -> {
                            var hasError = false
                            
                            if (newPassword.isBlank()) {
                                passwordError = "La contraseña es requerida"
                                hasError = true
                            } else if (newPassword.length < 6) {
                                passwordError = "Mínimo 6 caracteres"
                                hasError = true
                            }
                            
                            if (confirmPassword != newPassword) {
                                confirmError = "Las contraseñas no coinciden"
                                hasError = true
                            }
                            
                            if (!hasError) {
                                authViewModel.resetPassword(newPassword) {
                                    currentStep = ForgotPasswordStep.SUCCESS
                                }
                            }
                        }
                        ForgotPasswordStep.SUCCESS -> {
                            nav.navigate("login") {
                                popUpTo("forgot_password") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentStep == ForgotPasswordStep.SUCCESS) 
                        SecondaryGreen else AccentOrange
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = when (currentStep) {
                            ForgotPasswordStep.EMAIL -> "Enviar código"
                            ForgotPasswordStep.TOKEN -> "Verificar"
                            ForgotPasswordStep.NEW_PASSWORD -> "Cambiar contraseña"
                            ForgotPasswordStep.SUCCESS -> "Iniciar sesión"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

