package com.fairlaunch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.fairlaunch.ui.navigation.AppNavigation
import com.fairlaunch.ui.theme.FairLaunchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FairLaunchTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
