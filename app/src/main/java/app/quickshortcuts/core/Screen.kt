package app.quickshortcuts.core

// Screen.kt
sealed class Screen(val route: String) {
    object Main: Screen("main_screen")
    object SpeedDial: Screen("speed_dial_screen")
    object SpeedDialIntro: Screen("speed_dial_intro_screen")
}