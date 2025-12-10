package com.example.japuraroutef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.local.TokenManager
import com.example.japuraroutef.repository.AuthRepository
import com.example.japuraroutef.ui.ExtendedSplashScreen
import com.example.japuraroutef.ui.HomeScreen
import com.example.japuraroutef.ui.MapScreen
import com.example.japuraroutef.ui.RegistrationScreen
import com.example.japuraroutef.ui.screens.GetStartedScreen
import com.example.japuraroutef.ui.screens.LoginScreen
import com.example.japuraroutef.ui.theme.JapuraRouteFTheme
import com.example.japuraroutef.ui.theme.ThemePreferences
import com.example.japuraroutef.utils.ToastManager
import com.example.japuraroutef.viewmodel.LoginViewModel
import com.example.japuraroutef.viewmodel.LoginViewModelFactory
import com.example.japuraroutef.viewmodel.RegistrationViewModel
import com.example.japuraroutef.viewmodel.RegistrationViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)
        tokenManager = TokenManager.getInstance(this)

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
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                    com.example.japuraroutef.utils.ToastHost()
                }
            }
        }
    }

    @Composable
    private fun AppNavigation() {
        // Check authentication status after splash
        val isAuthenticated = tokenManager.isLoggedIn()

        var currentScreen by remember {
            mutableStateOf<Screen>(Screen.Splash)
        }

        // Initialize dependencies
        val toastManager = ToastManager.getInstance(this)
        val apiService = ApiService.create(this)
        val authRepository = AuthRepository(apiService, tokenManager)

        when (currentScreen) {
            Screen.Splash -> ExtendedSplashScreen(
                onSplashFinished = {
                    currentScreen = if (isAuthenticated) {
                        Screen.Home
                    } else {
                        Screen.GetStarted
                    }
                }
            )

            Screen.GetStarted -> GetStartedScreen(
                onGetStartedClick = {
                    currentScreen = Screen.Login
                }
            )

            Screen.Login -> {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(authRepository, toastManager)
                )

                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegister = {
                        currentScreen = Screen.Registration
                    },
                    onLoginSuccess = {
                        currentScreen = Screen.Home
                    }
                )
            }

            Screen.Registration -> {
                val registrationViewModel: RegistrationViewModel = viewModel(
                    factory = RegistrationViewModelFactory(authRepository)
                )

                RegistrationScreen(
                    viewModel = registrationViewModel,
                    onRegistrationSuccess = { token ->
                        // Clear the saved token and redirect to login
                        tokenManager.clearToken()
                        toastManager.showSuccess("Account created successfully! Please sign in.")
                        currentScreen = Screen.Login
                    },
                    onBackToLogin = {
                        currentScreen = Screen.Login
                    }
                )
            }

            Screen.Home -> HomeScreen(
                onNavigateToMap = { currentScreen = Screen.Map },
                onNavigateToSchedule = { currentScreen = Screen.ClassSchedule },
                onLogout = {
                    // Clear authentication data
                    tokenManager.clearAuth()
                    // Navigate to GetStarted screen
                    currentScreen = Screen.GetStarted
                }
            )

            Screen.Map -> MapScreen(
                onNavigateBack = { currentScreen = Screen.Home }
            )

            Screen.ClassSchedule -> com.example.japuraroutef.ui.ClassScheduleScreen(
                onNavigateBack = { currentScreen = Screen.Home }
            )
        }
    }

    private sealed class Screen {
        data object Splash : Screen()
        data object GetStarted : Screen()
        data object Login : Screen()
        data object Registration : Screen()
        data object Home : Screen()
        data object Map : Screen()
        data object ClassSchedule : Screen()
    }

}