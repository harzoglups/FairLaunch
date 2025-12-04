package com.fairlaunch.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Map : Screen("map")
    data object Settings : Screen("settings")
}
