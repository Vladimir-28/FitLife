package mx.edu.utez.fitlife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mx.edu.utez.fitlife.ui.theme.PrimaryBlue

@Composable
fun LoginScreen(nav: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

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

            Text("Welcome back!", color = Color.White)
        }

        // BODY
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text("Sign In", style = MaterialTheme.typography.headlineSmall)

            Text(
                "Enter your credentials to access your account",
                color = Color.Gray
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email") },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter your password") },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotEmpty())
                Text(error, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(14.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),

                onClick = {
                    if (email == "admin" && password == "123") {
                        nav.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        error = "Correo o contraseña incorrectos"
                    }
                }
            ) {
                Text("Sign In")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Text("Don't have an account? ")

                TextButton(
                    onClick = {
                        nav.navigate("register")
                    }
                ) {
                    Text("Sign up")
                }
            }
        }
    }
}
