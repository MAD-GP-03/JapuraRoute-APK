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
        private const val USER_UNI_YEAR_KEY = "user_uni_key"
        private const val USER_FOCUS_AREA_KEY = "user_focus_area_key"

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
            .putString(USER_UNI_YEAR_KEY, user.uniYear)
            .putString(USER_FOCUS_AREA_KEY, user.focusArea)
            .apply()
    }

    fun getUser(): User? {
        val id = prefs.getString(USER_ID_KEY, null)
        val email = prefs.getString(USER_EMAIL_KEY, null)
        val name = prefs.getString(USER_NAME_KEY, null)
        val uniYear = prefs.getString(USER_UNI_YEAR_KEY, null)
        val focusArea = prefs.getString(USER_FOCUS_AREA_KEY, null)


        return if (id != null && email != null) {
            User(id, email, name, uniYear,focusArea)
        } else {
            null
        }
    }

    fun clearUser() {
        prefs.edit()
            .remove(USER_ID_KEY)
            .remove(USER_EMAIL_KEY)
            .remove(USER_NAME_KEY)
            .remove(USER_UNI_YEAR_KEY)
            .remove(USER_FOCUS_AREA_KEY)
            .apply()
    }

    fun clearAuth() {
        clearToken()
        clearUser()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun getUserName(): String? {
        return prefs.getString(USER_NAME_KEY, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL_KEY, null)
    }

    fun getUserUniYear(): String? {
        return prefs.getString(USER_UNI_YEAR_KEY, null)
    }

    fun getUserFocusArea(): String? {
        return prefs.getString(USER_FOCUS_AREA_KEY, null)
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID_KEY, null)
    }
}
