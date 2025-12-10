package com.example.japuraroutef.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japuraroutef.model.LoginRequest
import com.example.japuraroutef.repository.AuthRepository
import com.example.japuraroutef.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val toastManager: ToastManager
) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var passwordVisible by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var emailError by mutableStateOf<String?>(null)
        private set

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun onEmailChange(newEmail: String) {
        email = newEmail
        // Clear error when user starts typing
        if (emailError != null) {
            emailError = null
        }
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun validateEmail(): String? {
        val value = email.trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            value.isBlank() -> "Email is required"
            !value.matches(Regex(emailPattern)) -> "Please enter a valid email address"
            else -> null
        }
    }

    fun login(onSuccess: () -> Unit) {
        // Validate email
        emailError = validateEmail()
        if (emailError != null) {
            toastManager.showError(emailError!!)
            return
        }

        // Validate password
        if (password.isBlank()) {
            toastManager.showError("Please enter password")
            return
        }

        viewModelScope.launch {
            isLoading = true

            val result = repository.login(LoginRequest(email, password))

            result.fold(
                onSuccess = { response ->
                    toastManager.showSuccess("Login successful!")
                    _loginSuccess.value = true
                    onSuccess()
                },
                onFailure = { error ->
                    toastManager.showError(error.message ?: "Login failed")
                }
            )

            isLoading = false
        }
    }
}

