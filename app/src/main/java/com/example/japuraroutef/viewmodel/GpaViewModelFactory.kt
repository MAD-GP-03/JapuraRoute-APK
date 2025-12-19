package com.example.japuraroutef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.japuraroutef.repository.GpaRepository

class GpaViewModelFactory(
    private val repository: GpaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GpaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GpaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

