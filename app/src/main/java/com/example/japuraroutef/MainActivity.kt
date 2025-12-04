package com.example.japuraroutef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.japuraroutef.ui.HomeScreen
import com.example.japuraroutef.ui.MapScreen
import com.example.japuraroutef.ui.theme.JapuraRouteFTheme
import com.example.japuraroutef.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            val useSystemTheme by themePreferences.useSystemTheme.collectAsState(initial = true)
            val darkMode by themePreferences.isDarkMode.collectAsState(initial = null)
            val amoledMode by themePreferences.useAmoledMode.collectAsState(initial = false)

            val isDarkTheme = when {
                useSystemTheme -> systemDarkTheme
                darkMode != null -> darkMode!!
                else -> systemDarkTheme
            }

            JapuraRouteFTheme(
                darkTheme = isDarkTheme,
                amoledMode = amoledMode
            ) {
                AppNavigation()
            }
        }
    }

    @Composable
    private fun AppNavigation() {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        when (currentScreen) {
            Screen.Home -> HomeScreen(onNavigateToMap = { currentScreen = Screen.Map })
            Screen.Map -> MapScreen(onNavigateBack = { currentScreen = Screen.Home })
        }
    }

    private sealed class Screen {
        data object Home : Screen()
        data object Map : Screen()
    }

}