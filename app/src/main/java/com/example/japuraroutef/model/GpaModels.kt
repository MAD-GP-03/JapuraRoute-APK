package com.example.japuraroutef.model

import com.google.gson.annotations.SerializedName

// Enums
enum class SemesterId {
    @SerializedName("FIRST")
    FIRST,
    @SerializedName("SECOND")
    SECOND,
    @SerializedName("THIRD")
    THIRD,
    @SerializedName("FOURTH")
    FOURTH,
    @SerializedName("FIFTH")
    FIFTH,
    @SerializedName("SIXTH")
    SIXTH,
    @SerializedName("SEVENTH")
    SEVENTH,
    @SerializedName("EIGHTH")
    EIGHTH;

    fun getDisplayName(): String {
        return when (this) {
            FIRST -> "Year 1 - Semester 1"
            SECOND -> "Year 1 - Semester 2"
            THIRD -> "Year 2 - Semester 1"
            FOURTH -> "Year 2 - Semester 2"
            FIFTH -> "Year 3 - Semester 1"
            SIXTH -> "Year 3 - Semester 2"
            SEVENTH -> "Year 4 - Semester 1"
            EIGHTH -> "Year 4 - Semester 2"
        }
    }

    fun requiresFocusArea(): Boolean {
        return this in listOf(FIFTH, SIXTH, SEVENTH, EIGHTH)
    }
}

enum class Grade(val value: String, val gpaValue: Float) {
    @SerializedName("A_PLUS") A_PLUS("A+", 4.00f),
    @SerializedName("A") A("A", 4.00f),
    @SerializedName("A_MINUS") A_MINUS("A-", 3.70f),
    @SerializedName("B_PLUS") B_PLUS("B+", 3.30f),
    @SerializedName("B") B("B", 3.00f),
    @SerializedName("B_MINUS") B_MINUS("B-", 2.70f),
    @SerializedName("C_PLUS") C_PLUS("C+", 2.30f),
    @SerializedName("C") C("C", 2.00f),
    @SerializedName("C_MINUS") C_MINUS("C-", 1.70f),
    @SerializedName("D_PLUS") D_PLUS("D+", 1.30f),
    @SerializedName("D") D("D", 1.00f),
    @SerializedName("E") E("E", 0.00f);

    companion object {
        fun fromString(value: String): Grade? {
            return entries.find { it.value == value }
        }
    }
}

// DTOs

data class Subject(
    @SerializedName("subjectName")
    val subjectName: String,
    val credits: Float,
    val grade: String
)

data class ModuleResult(
    @SerializedName("module_code")
    val moduleCode: String,
    @SerializedName("module_name")
    val moduleName: String,
    val grade: Grade,
    val credit: Int
)

data class CreateSemesterGpaRequest(
    val semesterId: SemesterId,
    val semesterName: String,
    val subjects: List<Subject>
)

data class SemesterGpaResponse(
    val id: String,
    val userId: String,
    val semesterId: SemesterId,
    val semesterName: String,
    val subjects: List<Subject>,
    val totalCredits: Float,
    val gpa: Float,
    val createdAt: String?,
    val updatedAt: String?
)

data class CgpaResponse(
    val totalCredits: Float,
    val cgpa: Float
)

data class BatchAverageResponse(
    val uniYear: String,
    val totalStudents: Int,
    val studentsWithGpa: Int,
    val averageGpa: Float,
    val studentsWithoutGpa: Int
)

data class GpaApiResponse<T>(
    val status: Boolean,
    val message: String?,
    val data: T?
)

// Module from API
data class Module(
    val moduleCode: String,
    val moduleName: String,
    val credits: Int,
    val focusArea: List<String>,
    @SerializedName("semetserId")  // Note: API has typo "semetserId"
    val semesterId: SemesterId,
    val id: String,
    val createdAt: String?,
    val createdBy: String?,
    val updatedAt: String?,
    val updatedBy: String?
) {
    fun toSubject(): Subject {
        return Subject(
            subjectName = "$moduleCode - $moduleName",
            credits = credits.toFloat(),
            grade = "A+" // Default grade for preloaded modules
        )
    }
}

data class ModulesApiResponse(
    val status: Boolean,
    val message: String?,
    val data: List<Module>?
)

