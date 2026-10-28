package com.example.pucaos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.compose.NavHost
import com.example.pucaos.ui.theme.PucaOSTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PucaOSTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
                ) {
                    composable(
                        route = "main",
                    ) {
                        MainScreen(onNavigateToSchedule = {navController.navigate("schedule")})
                    }
                    composable(
                        route = "schedule"
                    ) {
                        ScheduleScreen(onBack = {navController.navigate("main")})
                    }
                }
            }
        }
    }
}

