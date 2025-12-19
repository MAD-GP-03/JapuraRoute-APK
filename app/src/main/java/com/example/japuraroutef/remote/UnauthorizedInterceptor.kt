package com.example.japuraroutef.remote

import android.util.Log
import com.example.japuraroutef.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to handle 401 (Unauthorized) and 403 (Forbidden) responses.
 * When these status codes are received, it clears the auth token and notifies
 * the app to redirect to login screen.
 */
class UnauthorizedInterceptor(
    private val tokenManager: TokenManager,
    private val onUnauthorized: () -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Check if response is 401 or 403
        if (response.code == 401 || response.code == 403) {
            Log.w(TAG, "Received ${response.code} response from ${request.url}. Clearing auth and redirecting to login.")

            // Clear authentication data
            tokenManager.clearAuth()

            // Notify the callback to handle navigation
            onUnauthorized()
        }

        return response
    }

    companion object {
        private const val TAG = "UnauthorizedInterceptor"
    }
}

