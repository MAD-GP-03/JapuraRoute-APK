package com.example.japuraroutef.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.japuraroutef.local.TokenManager
import com.example.japuraroutef.model.FocusArea
import com.example.japuraroutef.model.TimetableData
import com.example.japuraroutef.model.TimetableEntry
import com.example.japuraroutef.model.matchesFocusArea
import com.example.japuraroutef.repository.TimetableRepository
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

/**
 * ViewModel for HomeScreen
 * Manages timetable fetching and current class information
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "HomeViewModel"
    private val tokenManager = TokenManager(application)
    private val timetableRepository = TimetableRepository(application)

    // UI State
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val timetableData = mutableStateOf<TimetableData?>(null)
    val currentClassInfo = mutableStateOf<ClassInfo?>(null)

    // User info from token storage
    val userName = mutableStateOf<String?>(null)
    val userUniYear = mutableStateOf<String?>(null)
    val userFocusArea = mutableStateOf<FocusArea?>(null)

    init {
        loadUserInfo()
    }

    /**
     * Load user information from TokenManager
     */
    private fun loadUserInfo() {
        userName.value = tokenManager.getUserName()
        userUniYear.value = tokenManager.getUserUniYear()

        // Parse focus area string to enum
        val focusAreaStr = tokenManager.getUserFocusArea()
        userFocusArea.value = focusAreaStr?.let {
            try {
                FocusArea.valueOf(it)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid focus area: $it", e)
                null
            }
        }

        // Fetch timetable if we have university year
        userUniYear.value?.let { year ->
            fetchTimetable(year)
        }
    }

    /**
     * Fetch timetable from API
     */
    fun fetchTimetable(uniYear: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = timetableRepository.getTimetableByYear(uniYear)

            result.onSuccess { data ->
                timetableData.value = data
                updateCurrentClassInfo()
                Log.d(TAG, "Timetable loaded successfully")
            }

            result.onFailure { error ->
                errorMessage.value = error.message
                Log.e(TAG, "Failed to load timetable: ${error.message}")
            }

            isLoading.value = false
        }
    }

    /**
     * Update current class information based on current day and time
     */
    private fun updateCurrentClassInfo() {
        val data = timetableData.value ?: return
        val userFocus = userFocusArea.value

        // Get current day and time
        val currentDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val currentTime = LocalTime.now()

        // Filter classes for today that match user's focus area
        val todayClasses = data.timetable.timetable
            .filter { it.day.equals(currentDay, ignoreCase = true) }
            .filter { it.matchesFocusArea(userFocus) }
            .sortedBy { LocalTime.parse(it.start_time) }

        if (todayClasses.isEmpty()) {
            currentClassInfo.value = ClassInfo.NoClassToday
            return
        }

        // Find current or next class
        for (classEntry in todayClasses) {
            val startTime = LocalTime.parse(classEntry.start_time)
            val endTime = LocalTime.parse(classEntry.end_time)

            when {
                // Currently in this class
                currentTime.isAfter(startTime) && currentTime.isBefore(endTime) -> {
                    currentClassInfo.value = ClassInfo.InClass(
                        moduleName = classEntry.module_name,
                        moduleCode = classEntry.module_code,
                        location = classEntry.location,
                        endTime = classEntry.end_time,
                        type = classEntry.type,
                        lecturer = classEntry.lecturer
                    )
                    return
                }
                // This class is upcoming
                currentTime.isBefore(startTime) -> {
                    currentClassInfo.value = ClassInfo.NextClass(
                        moduleName = classEntry.module_name,
                        moduleCode = classEntry.module_code,
                        location = classEntry.location,
                        startTime = classEntry.start_time,
                        type = classEntry.type,
                        lecturer = classEntry.lecturer
                    )
                    return
                }
            }
        }

        // All classes for today are finished
        currentClassInfo.value = ClassInfo.ClassesFinished
    }

    /**
     * Get greeting based on time of day
     */
    fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    /**
     * Refresh timetable and class info
     */
    fun refresh() {
        userUniYear.value?.let { year ->
            fetchTimetable(year)
        }
    }
}

/**
 * Sealed class representing different class information states
 */
sealed class ClassInfo {
    data class NextClass(
        val moduleName: String,
        val moduleCode: String,
        val location: String,
        val startTime: String,
        val type: String,
        val lecturer: String
    ) : ClassInfo()

    data class InClass(
        val moduleName: String,
        val moduleCode: String,
        val location: String,
        val endTime: String,
        val type: String,
        val lecturer: String
    ) : ClassInfo()

    object NoClassToday : ClassInfo()
    object ClassesFinished : ClassInfo()
}

