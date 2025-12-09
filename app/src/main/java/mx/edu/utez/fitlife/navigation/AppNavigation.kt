package mx.edu.utez.fitlife.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utez.fitlife.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable(
            route = "add_activity"
        ) {
            AddEditActivityScreen(navController, activityId = null)
        }

        composable(
            route = "edit_activity/{activityId}",
            arguments = listOf(
                navArgument("activityId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getInt("activityId")
            AddEditActivityScreen(navController, activityId = activityId)
        }
    }
}
