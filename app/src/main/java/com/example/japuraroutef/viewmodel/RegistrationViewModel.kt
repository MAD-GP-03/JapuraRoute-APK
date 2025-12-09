package com.example.japuraroutef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japuraroutef.model.FocusArea
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse
import com.example.japuraroutef.model.UniYear
import com.example.japuraroutef.repository.AuthRepository
import com.example.japuraroutef.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep

    // Test ToastManager initialization
    init {
        android.util.Log.d("RegistrationViewModel", "RegistrationViewModel initialized")
        // Test toast to verify it's working - temporarily enabled for testing
        // ToastManager.showInfo("Registration system ready!")
    }

    // Step 1 fields
    val username = MutableStateFlow("")
    val fullName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    // Step 2 fields
    val phoneNumber = MutableStateFlow("")
    val address = MutableStateFlow("")
    val uniYear = MutableStateFlow<UniYear?>(null)
    val regNumber = MutableStateFlow("")
    val nic = MutableStateFlow("")
    val selectedFocusArea = MutableStateFlow(FocusArea.SOFTWARE)

    fun canSelectFocusArea(): Boolean {
        return uniYear.value in listOf(UniYear.THIRD_YEAR, UniYear.FOURTH_YEAR)
    }

    fun getFocusArea(): String {
        return if (canSelectFocusArea()) {
            selectedFocusArea.value.name
        } else {
            FocusArea.COMMON.name
        }
    }

    fun validateStep1(): Boolean {
        return validateUsername() == null &&
                validateFullName() == null &&
                validateEmail() == null &&
                validatePassword() == null &&
                validateConfirmPassword() == null
    }

    // Individual field validation functions that return error messages
    fun validateUsername(): String? {
        val value = username.value.trim()
        return when {
            value.isBlank() -> "Username is required"
            value.length < 3 -> "Username must be at least 3 characters"
            value.length > 50 -> "Username must not exceed 50 characters"
            !value.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscores"
            else -> null
        }
    }

    fun validateFullName(): String? {
        val value = fullName.value.trim()
        return when {
            value.isBlank() -> "Full name is required"
            value.length < 2 -> "Full name must be at least 2 characters"
            value.length > 100 -> "Full name must not exceed 100 characters"
            else -> null
        }
    }

    fun validateEmail(): String? {
        val value = email.value.trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            value.isBlank() -> "Email is required"
            !value.matches(Regex(emailPattern)) -> "Please enter a valid email address"
            value.length > 100 -> "Email must not exceed 100 characters"
            else -> null
        }
    }

    fun validatePassword(): String? {
        val value = password.value
        return when {
            value.isBlank() -> "Password is required"
            value.length < 8 -> "Password must be at least 8 characters"
            value.length > 100 -> "Password must not exceed 100 characters"
            !value.matches(Regex(".*[a-z].*")) -> "Password must contain at least one lowercase letter"
            !value.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter"
            !value.matches(Regex(".*\\d.*")) -> "Password must contain at least one digit"
            !value.matches(Regex(".*[@\$!%*?&].*")) -> "Password must contain at least one special character (@\$!%*?&)"
            else -> null
        }
    }

    fun validateConfirmPassword(): String? {
        val confirmValue = confirmPassword.value
        val passwordValue = password.value
        return when {
            confirmValue.isBlank() -> "Please confirm your password"
            confirmValue != passwordValue -> "Passwords do not match"
            else -> null
        }
    }

    fun validateStep2(): Boolean {
        return validatePhoneNumber() == null &&
                validateAddress() == null &&
                validateUniYear() == null &&
                validateRegNumber() == null &&
                validateNic() == null
    }

    // Step 2 validation functions
    fun validatePhoneNumber(): String? {
        val value = phoneNumber.value.trim()
        val phonePattern = "^[+]?[0-9]{10,15}$"
        return when {
            value.isBlank() -> "Phone number is required"
            !value.matches(Regex(phonePattern)) -> "Please enter a valid phone number (10-15 digits)"
            else -> null
        }
    }

    fun validateAddress(): String? {
        val value = address.value.trim()
        return when {
            value.isBlank() -> "Address is required"
            value.length < 5 -> "Address must be at least 5 characters"
            value.length > 200 -> "Address must not exceed 200 characters"
            else -> null
        }
    }

    fun validateUniYear(): String? {
        return if (uniYear.value == null) "Please select your university year" else null
    }

    fun validateRegNumber(): String? {
        val value = regNumber.value.trim()
        return when {
            value.isBlank() -> "Registration number is required"
            value.length < 3 -> "Registration number must be at least 3 characters"
            value.length > 20 -> "Registration number must not exceed 20 characters"
            else -> null
        }
    }

    fun validateNic(): String? {
        val value = nic.value.trim()
        return when {
            value.isBlank() -> "NIC number is required"
            value.length < 5 -> "NIC number must be at least 5 characters"
            value.length > 20 -> "NIC number must not exceed 20 characters"
            else -> null
        }
    }

    fun nextStep() {
        if (_currentStep.value == 1 && validateStep1()) {
            _currentStep.value = 2
        }
    }

    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    fun register() {
        if (!validateStep2()) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading

            val request = RegisterRequest(
                username = username.value,
                fullName = fullName.value,
                email = email.value,
                password = password.value,
                phoneNumber = phoneNumber.value,
                address = address.value,
                role = "STUDENT",
                uniYear = uniYear.value!!.name,
                regNumber = regNumber.value,
                department = "IT",
                focusArea = getFocusArea(),
                nic = nic.value
            )

            // Log the request data for debugging
            android.util.Log.d("RegistrationViewModel", "Sending registration request:")
            android.util.Log.d("RegistrationViewModel", "Username: ${request.username}")
            android.util.Log.d("RegistrationViewModel", "FullName: ${request.fullName}")
            android.util.Log.d("RegistrationViewModel", "Email: ${request.email}")
            android.util.Log.d("RegistrationViewModel", "PhoneNumber: ${request.phoneNumber}")
            android.util.Log.d("RegistrationViewModel", "Address: ${request.address}")
            android.util.Log.d("RegistrationViewModel", "Role: ${request.role}")
            android.util.Log.d("RegistrationViewModel", "UniYear: ${request.uniYear}")
            android.util.Log.d("RegistrationViewModel", "RegNumber: ${request.regNumber}")
            android.util.Log.d("RegistrationViewModel", "Department: ${request.department}")
            android.util.Log.d("RegistrationViewModel", "FocusArea: ${request.focusArea}")
            android.util.Log.d("RegistrationViewModel", "NIC: ${request.nic}")

            val result = authRepository.register(request)
            _registrationState.value = if (result.isSuccess) {
                android.util.Log.d("RegistrationViewModel", "Registration successful!")
                ToastManager.showSuccess("Account created successfully! Welcome to FOTGo!")
                RegistrationState.Success(result.getOrNull()!!)
            } else {
                val exception = result.exceptionOrNull()
                val errorMessage = exception?.message ?: "Registration failed"

                // Enhanced error logging
                android.util.Log.e("RegistrationViewModel", "Registration failed with error: $errorMessage")
                android.util.Log.e("RegistrationViewModel", "Exception type: ${exception?.javaClass?.simpleName}")

                // Parse error message from backend response
                val userErrorMessage = when {
                    errorMessage.contains("Email already exists", ignoreCase = true) -> "This email is already registered. Please use a different email."
                    errorMessage.contains("Username already exists", ignoreCase = true) -> "This username is already taken. Please choose a different one."
                    errorMessage.contains("400", ignoreCase = true) && errorMessage.contains("error", ignoreCase = true) -> {
                        // Try to extract the actual error message from JSON response
                        try {
                            val errorStart = errorMessage.indexOf("\"error\":\"") + 9
                            val errorEnd = errorMessage.indexOf("\"", errorStart)
                            if (errorStart > 8 && errorEnd > errorStart) {
                                errorMessage.substring(errorStart, errorEnd)
                            } else "Invalid information provided. Please check your details."
                        } catch (e: Exception) {
                            "Invalid information provided. Please check your details."
                        }
                    }
                    errorMessage.contains("409", ignoreCase = true) -> "Username or email already exists."
                    errorMessage.contains("network", ignoreCase = true) -> "Network error. Please check your connection."
                    else -> "Registration failed. Please try again."
                }

                ToastManager.showError(userErrorMessage)
                RegistrationState.Error(errorMessage)
            }
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val response: RegisterResponse) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}