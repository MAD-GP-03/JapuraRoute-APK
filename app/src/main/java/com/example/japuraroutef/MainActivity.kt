package com.example.japuraroutef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppNavigation()
        }
    }

    @Composable
    fun AppNavigation() {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        when (currentScreen) {
            Screen.Home -> {
                com.example.japuraroutef.ui.HomeScreen(
                    onNavigateToMap = { currentScreen = Screen.Map }
                )
            }
            Screen.Map -> {
                com.example.japuraroutef.ui.MapScreen(
                    onNavigateBack = { currentScreen = Screen.Home }
                )
            }
        }
    }

    sealed class Screen {
        object Home : Screen()
        object Map : Screen()
    }

}