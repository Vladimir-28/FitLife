package mx.edu.utez.fitlife.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import mx.edu.utez.fitlife.ui.screens.*
import mx.edu.utez.fitlife.ui.theme.FitLifeTheme

@Composable
fun AppNavigation() {
    FitLifeTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "login",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            composable("login") {
                LoginScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController)
            }
            
            composable("forgot_password") {
                ForgotPasswordScreen(navController)
            }

            composable("home") {
                HomeScreen(navController)
            }

            composable("profile") {
                ProfileScreen(navController)
            }

            composable(route = "add_activity") {
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
}
