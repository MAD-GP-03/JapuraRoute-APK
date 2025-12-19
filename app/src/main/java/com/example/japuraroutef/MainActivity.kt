package com.example.japuraroutef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.japuraroutef.repository.GpaRepository
import com.example.japuraroutef.ui.ExtendedSplashScreen
import com.example.japuraroutef.ui.HomeScreen
import com.example.japuraroutef.ui.MapScreen
import com.example.japuraroutef.ui.ClassScheduleScreen
import com.example.japuraroutef.ui.RegistrationScreen
import com.example.japuraroutef.ui.GpaOverviewScreen
import com.example.japuraroutef.ui.GpaSemesterDetailScreen
import com.example.japuraroutef.ui.screens.GetStartedScreen
import com.example.japuraroutef.ui.screens.LoginScreen
import com.example.japuraroutef.ui.theme.JapuraRouteFTheme
import com.example.japuraroutef.ui.theme.ThemePreferences
import com.example.japuraroutef.model.SemesterId
import com.example.japuraroutef.model.FocusArea
import com.example.japuraroutef.viewmodel.GpaViewModel
import com.example.japuraroutef.utils.ToastManager
import com.example.japuraroutef.utils.AuthStateManager
import com.example.japuraroutef.viewmodel.LoginViewModel
import com.example.japuraroutef.viewmodel.LoginViewModelFactory
import com.example.japuraroutef.viewmodel.RegistrationViewModel
import com.example.japuraroutef.viewmodel.RegistrationViewModelFactory
import com.example.japuraroutef.viewmodel.GpaViewModelFactory

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
        // Track authentication status as state
        var isAuthenticated by remember { mutableStateOf(tokenManager.isLoggedIn()) }

        var currentScreen by remember {
            mutableStateOf<Screen>(Screen.Splash)
        }

        // Initialize dependencies
        val toastManager = ToastManager.getInstance(this)
        val apiService = ApiService.create(this)
        val authRepository = AuthRepository(apiService, tokenManager)

        // Create single GpaViewModel instance only when authenticated
        val gpaRepository = remember { GpaRepository(apiService) }
        val sharedGpaViewModel: GpaViewModel? = if (isAuthenticated) {
            viewModel(
                factory = GpaViewModelFactory(gpaRepository),
                key = "gpa_vm_${tokenManager.getToken()}" // Unique key per user
            )
        } else {
            null
        }

        // Fetch data when user logs in
        LaunchedEffect(isAuthenticated) {
            if (isAuthenticated) {
                sharedGpaViewModel?.fetchAllData()
            }
        }

        // Listen for unauthorized (401/403) events from API interceptor
        LaunchedEffect(Unit) {
            AuthStateManager.logoutEvent.collect {
                // Clear authentication state
                isAuthenticated = false
                // Navigate to GetStarted screen
                currentScreen = Screen.GetStarted
                // Show toast message
                toastManager.showError("Session expired. Please sign in again.")
            }
        }

        when (currentScreen) {
            Screen.Splash -> ExtendedSplashScreen(
                onSplashFinished = {
                    // Check auth state when splash finishes
                    isAuthenticated = tokenManager.isLoggedIn()
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
                        // Update authentication state to trigger ViewModel creation and data fetching
                        isAuthenticated = true
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
                onNavigateToGrades = { currentScreen = Screen.GpaOverview },
                onLogout = {
                    // Clear authentication data
                    tokenManager.clearAuth()
                    // Update authentication state (this will destroy the ViewModel)
                    isAuthenticated = false
                    // Navigate to GetStarted screen
                    currentScreen = Screen.GetStarted
                }
            )

            Screen.Map -> MapScreen(
                onNavigateBack = { currentScreen = Screen.Home }
            )

            Screen.ClassSchedule -> ClassScheduleScreen(
                onNavigateBack = { currentScreen = Screen.Home }
            )

            Screen.GpaOverview -> {
                sharedGpaViewModel?.let { viewModel ->
                    GpaOverviewScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.Home },
                        onNavigateToDetail = { semesterId ->
                            currentScreen = Screen.GpaSemesterDetail(semesterId)
                        },
                        onNavigateToStatistics = { currentScreen = Screen.GpaStatistics }
                    )
                }
            }

            Screen.GpaStatistics -> {
                sharedGpaViewModel?.let { viewModel ->
                    com.example.japuraroutef.ui.GpaStatisticsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.GpaOverview }
                    )
                }
            }

            is Screen.GpaSemesterDetail -> {
                sharedGpaViewModel?.let { viewModel ->
                    val detailScreen = currentScreen as Screen.GpaSemesterDetail

                    // Get user's focus area from TokenManager
                    val userFocusAreaString = tokenManager.getUserFocusArea()
                    val userFocusArea = userFocusAreaString?.let {
                        try {
                            FocusArea.valueOf(it)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    GpaSemesterDetailScreen(
                        viewModel = viewModel,
                        semesterId = detailScreen.semesterId,
                        onNavigateBack = { currentScreen = Screen.GpaOverview },
                        userFocusArea = userFocusArea
                    )
                }
            }
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
        data object GpaOverview : Screen()
        data object GpaStatistics : Screen()
        data class GpaSemesterDetail(val semesterId: SemesterId) : Screen()
    }

}
