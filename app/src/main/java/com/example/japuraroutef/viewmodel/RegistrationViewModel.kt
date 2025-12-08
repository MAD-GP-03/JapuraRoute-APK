package com.example.japuraroutef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japuraroutef.model.FocusArea
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse
import com.example.japuraroutef.model.UniYear
import com.example.japuraroutef.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep

    // Step 1 fields
    val username = MutableStateFlow("")
    val fullName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    // Step 2 fields
    val phoneNumber = MutableStateFlow("")
    val address = MutableStateFlow("")
    val uniYear = MutableStateFlow<UniYear?>(null)
    val regNumber = MutableStateFlow("")
    val nic = MutableStateFlow("")
    val selectedFocusArea = MutableStateFlow(FocusArea.COMMON)

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
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"

        return username.value.length >= 3 &&
                username.value.matches(Regex("^[a-zA-Z0-9_]+$")) &&
                fullName.value.length >= 2 &&
                email.value.matches(Regex(emailPattern)) &&
                password.value.matches(Regex(passwordPattern))
    }

    fun validateStep2(): Boolean {
        val phonePattern = "^[+]?[0-9]{10,15}$"

        return phoneNumber.value.matches(Regex(phonePattern)) &&
                address.value.length >= 5 &&
                uniYear.value != null &&
                regNumber.value.length in 3..20 &&
                nic.value.length in 5..20
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
                uniYear = uniYear.value!!.name,
                regNumber = regNumber.value,
                focusArea = getFocusArea(),
                nic = nic.value
            )

            val result = authRepository.register(request)
            _registrationState.value = if (result.isSuccess) {
                RegistrationState.Success(result.getOrNull()!!)
            } else {
                RegistrationState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }

        fun resetState() {
            _registrationState.value = RegistrationState.Idle
        }
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val response: RegisterResponse) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}