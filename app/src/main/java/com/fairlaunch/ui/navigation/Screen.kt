package com.fairlaunch.ui.navigation

sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object Settings : Screen("settings")
}
