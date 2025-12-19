package com.example.japuraroutef.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Singleton to manage authentication state changes across the app.
 * Emits events when user needs to be logged out due to 401/403 responses.
 */
object AuthStateManager {

    private val _logoutEvent = MutableSharedFlow<Unit>(replay = 0)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    /**
     * Triggers a logout event that should navigate to login screen
     */
    suspend fun triggerLogout() {
        _logoutEvent.emit(Unit)
    }
}

