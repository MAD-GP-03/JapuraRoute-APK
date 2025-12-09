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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.japuraroutef.ui.ExtendedSplashScreen
import com.example.japuraroutef.ui.HomeScreen
import com.example.japuraroutef.ui.MapScreen
import com.example.japuraroutef.ui.RegistrationScreen
import com.example.japuraroutef.ui.theme.JapuraRouteFTheme
import com.example.japuraroutef.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()

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
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

        when (currentScreen) {
            Screen.Splash -> ExtendedSplashScreen(
                onSplashFinished = { currentScreen = Screen.Registration }  // Navigate to Registration for testing
            )
            Screen.Home -> HomeScreen(onNavigateToMap = { currentScreen = Screen.Map })
            Screen.Map -> MapScreen(onNavigateBack = { currentScreen = Screen.Home })
            Screen.Registration -> RegistrationScreen(
                onRegistrationSuccess = { token ->
                    // Handle successful registration (save token, navigate to home)
                    println("Registration successful! Token: $token")
                    currentScreen = Screen.Home
                },
                onBackToLogin = {
                    // For now, go back to home
                    currentScreen = Screen.Home
                }
            )
        }
    }

    private sealed class Screen {
        data object Splash : Screen()
        data object Home : Screen()
        data object Map : Screen()
        data object Registration : Screen()
    }

}