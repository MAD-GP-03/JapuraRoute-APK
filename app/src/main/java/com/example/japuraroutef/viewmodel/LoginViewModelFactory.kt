package com.example.japuraroutef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.japuraroutef.repository.AuthRepository
import com.example.japuraroutef.utils.ToastManager

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val toastManager: ToastManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, toastManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

