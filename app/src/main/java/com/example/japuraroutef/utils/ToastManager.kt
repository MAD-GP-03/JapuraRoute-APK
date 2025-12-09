package com.example.japuraroutef.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ToastManager {
    private val _toastFlow = MutableSharedFlow<ToastMessage>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val toastFlow = _toastFlow.asSharedFlow()

    fun showSuccess(message: String) {
        android.util.Log.d("ToastManager", "Showing success message: $message")
        _toastFlow.tryEmit(ToastMessage(message, ToastType.SUCCESS))
    }

    fun showError(message: String) {
        android.util.Log.d("ToastManager", "Showing error message: $message")
        _toastFlow.tryEmit(ToastMessage(message, ToastType.ERROR))
    }

    fun showInfo(message: String) {
        android.util.Log.d("ToastManager", "Showing info message: $message")
        _toastFlow.tryEmit(ToastMessage(message, ToastType.INFO))
    }
}

data class ToastMessage(
    val message: String,
    val type: ToastType
)

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun ToastHost() {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        android.util.Log.d("ToastHost", "ToastHost started, listening for messages...")
        ToastManager.toastFlow.collect { toastMessage ->
            android.util.Log.d("ToastHost", "Received toast message: ${toastMessage.message} (${toastMessage.type})")
            try {
                snackbarHostState.showSnackbar(
                    message = toastMessage.message,
                    duration = when (toastMessage.type) {
                        ToastType.ERROR -> SnackbarDuration.Long
                        ToastType.SUCCESS -> SnackbarDuration.Short
                        ToastType.INFO -> SnackbarDuration.Short
                    }
                )
                android.util.Log.d("ToastHost", "Snackbar shown successfully")
            } catch (e: Exception) {
                android.util.Log.e("ToastHost", "Error showing snackbar: ${e.message}")
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) { snackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            containerColor = when {
                snackbarData.visuals.message.contains("success", ignoreCase = true) ||
                snackbarData.visuals.message.contains("Welcome", ignoreCase = true) ||
                snackbarData.visuals.message.contains("created", ignoreCase = true) ->
                    Color(0xFF4CAF50) // Success green

                snackbarData.visuals.message.contains("already", ignoreCase = true) ||
                snackbarData.visuals.message.contains("exists", ignoreCase = true) ||
                snackbarData.visuals.message.contains("failed", ignoreCase = true) ||
                snackbarData.visuals.message.contains("Invalid", ignoreCase = true) ||
                snackbarData.visuals.message.contains("error", ignoreCase = true) ->
                    Color(0xFFE53935) // Error red

                else -> Color(0xFF2196F3) // Info blue
            },
            contentColor = Color.White,
            shape = MaterialTheme.shapes.medium
        )
    }
}
