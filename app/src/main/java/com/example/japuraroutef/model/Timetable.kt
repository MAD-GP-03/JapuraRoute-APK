package com.example.japuraroutef.model

/**
 * Timetable API Response Model
 */
data class TimetableResponse(
    val status: Boolean,
    val message: String?,
    val data: TimetableData?
)

data class TimetableData(
    val id: String,
    val uniYear: String,
    val timetable: TimetableWrapper,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: String,
    val updatedBy: String
)

data class TimetableWrapper(
    val timetable: List<TimetableEntry>
)

data class TimetableEntry(
    val day: String,
    val type: String,
    val end_time: String,
    val lecturer: String,
    val location: String,
    val focus_area: List<String>,
    val start_time: String,
    val module_code: String,
    val module_name: String
)

/**
 * Helper function to check if a focus area matches the user's focus area
 */
fun TimetableEntry.matchesFocusArea(userFocusArea: FocusArea?): Boolean {
    // COMMON subjects (ITC) are for everyone
    if (focus_area.contains("ITC")) {
        return true
    }

    // If user has no specific focus area, only show COMMON subjects
    if (userFocusArea == null || userFocusArea == FocusArea.COMMON) {
        return false
    }

    // Map user's focus area to timetable focus area code
    val userFocusCode = when (userFocusArea) {
        FocusArea.SOFTWARE -> "ITS"
        FocusArea.NETWORKING -> "ITN"
        FocusArea.MULTIMEDIA -> "ITM"
        FocusArea.COMMON -> "ITC"
    }

    return focus_area.contains(userFocusCode)
}

/**
 * Get display text for class type
 */
fun TimetableEntry.getTypeDisplay(): String {
    return when (type) {
        "L" -> "Lecture"
        "P" -> "Practical"
        "T" -> "Tutorial"
        "L/T" -> "Lecture/Tutorial"
        "L/P" -> "Lecture/Practical"
        else -> type
    }
}

