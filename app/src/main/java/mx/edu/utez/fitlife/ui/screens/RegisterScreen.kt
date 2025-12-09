package mx.edu.utez.fitlife.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.utez.fitlife.ui.theme.PrimaryBlue
import mx.edu.utez.fitlife.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(nav: NavController) {
    val context = LocalContext.current
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

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    Column {

        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White, CircleShape)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "FitTracker",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // BODY
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text("Create Account", style = MaterialTheme.typography.headlineSmall)

            Text(
                "Enter your information to create your account",
                color = Color.Gray
            )

            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameError = null
                },
                placeholder = { Text("Full name") },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = null
                },
                placeholder = { Text("Enter your email") },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = null
                },
                placeholder = { Text("Enter your password") },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            OutlinedTextField(
                value = confirm,
                onValueChange = { 
                    confirm = it
                    confirmError = null
                },
                placeholder = { Text("Confirm your password") },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = confirmError != null,
                supportingText = confirmError?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            errorState?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading,
                onClick = {
                    var hasError = false
                    
                    if (name.isBlank()) {
                        nameError = "El nombre es requerido"
                        hasError = true
                    } else if (name.length < 3) {
                        nameError = "El nombre debe tener al menos 3 caracteres"
                        hasError = true
                    }
                    
                    if (email.isBlank()) {
                        emailError = "El email es requerido"
                        hasError = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Email inválido"
                        hasError = true
                    }
                    
                    if (password.isBlank()) {
                        passwordError = "La contraseña es requerida"
                        hasError = true
                    } else if (password.length < 6) {
                        passwordError = "La contraseña debe tener al menos 6 caracteres"
                        hasError = true
                    }
                    
                    if (confirm.isBlank()) {
                        confirmError = "Confirme la contraseña"
                        hasError = true
                    } else if (password != confirm) {
                        confirmError = "Las contraseñas no coinciden"
                        hasError = true
                    }
                    
                    if (!hasError) {
                        authViewModel.register(name.trim(), email.trim().lowercase(), password) {
                            nav.navigate("home") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Create Account")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Text("Already have an account? ")

                TextButton(
                    onClick = {
                        nav.popBackStack()
                    }
                ) {
                    Text("Sign in")
                }
            }
        }
    }
}
