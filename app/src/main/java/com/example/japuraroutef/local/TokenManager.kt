package com.example.japuraroutef.local

import android.content.Context
import android.content.SharedPreferences
import com.example.japuraroutef.model.User

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "japura_routef_prefs"
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_NAME_KEY = "user_name"

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenManager(context)
                INSTANCE = instance
                instance
            }
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    fun saveUser(user: User) {
        prefs.edit()
            .putString(USER_ID_KEY, user.id)
            .putString(USER_EMAIL_KEY, user.email)
            .putString(USER_NAME_KEY, user.name)
            .apply()
    }

    fun getUser(): User? {
        val id = prefs.getString(USER_ID_KEY, null)
        val email = prefs.getString(USER_EMAIL_KEY, null)
        val name = prefs.getString(USER_NAME_KEY, null)

        return if (id != null && email != null) {
            User(id, email, name)
        } else {
            null
        }
    }

    fun clearUser() {
        prefs.edit()
            .remove(USER_ID_KEY)
            .remove(USER_EMAIL_KEY)
            .remove(USER_NAME_KEY)
            .apply()
    }

    fun clearAuth() {
        clearToken()
        clearUser()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
