package app.quickshortcuts.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.quickshortcuts.ui.MainScreen
import app.quickshortcuts.ui.SpeedDialIntroScreen
import app.quickshortcuts.ui.SpeedDialScreen

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SpeedDialIntro.route) {
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.SpeedDialIntro.route,
        ) {
            SpeedDialIntroScreen(navController)
        }
        composable(
            route = Screen.SpeedDial.route,
        ) {
            SpeedDialScreen()
        }
    }
}
