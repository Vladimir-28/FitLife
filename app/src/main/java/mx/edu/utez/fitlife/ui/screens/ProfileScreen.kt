package mx.edu.utez.fitlife.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import mx.edu.utez.fitlife.ui.components.*
import mx.edu.utez.fitlife.ui.theme.*
import mx.edu.utez.fitlife.viewmodel.AuthViewModel

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
    
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Verificar sesión al cargar
    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }

    Column {

        Header("Mi Perfil")

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                Modifier
                    .size(90.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    currentUser?.name?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }

            Text(
                currentUser?.name ?: "Usuario",
                style = MaterialTheme.typography.titleLarge
            )
            Text(currentUser?.email ?: "email@ejemplo.com")

            Divider()

            Text("Información personal")

            Card {
                Column(Modifier.padding(12.dp)) {
                    Text("Nombre completo")
                    Text("María González")
                }
            }

            Text("Datos físicos")

            Row {
                InfoChip("Altura","165cm", BlueSoft,BlueAccent)
                InfoChip("Peso","62kg", RedSoft,RedAccent)
            }

            Text("Estadísticas")

            Row {
                InfoChip("Tiempo","42h", BlueSoft,BlueAccent)
                InfoChip("KM","15.2", RedSoft,RedAccent)
            }

            InfoChip("Pasos","34,567",OrangeSoft,OrangeAccent)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de cerrar sesión
            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }

        Spacer(Modifier.weight(1f))
        BottomPillBar(navController, "profile")
    }
}
