package com.example.japuraroutef.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
        private val USE_AMOLED_MODE = booleanPreferencesKey("use_amoled_mode")
    }

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE]
    }

    val useSystemTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USE_SYSTEM_THEME] ?: true // Default to system theme
    }

    val useAmoledMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USE_AMOLED_MODE] ?: false // Default to false
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    suspend fun setUseSystemTheme(useSystem: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_SYSTEM_THEME] = useSystem
        }
    }

    suspend fun setAmoledMode(useAmoled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_AMOLED_MODE] = useAmoled
        }
    }
}

