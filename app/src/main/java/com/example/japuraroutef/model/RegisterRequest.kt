package com.example.japuraroutef.model

data class RegisterRequest (
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val address: String,
    val role: String = "STUDENT",
    val uniYear: String,
    val regNumber: String,
    val department: String = "IT",
    val focusArea: String,
    val nic: String
)

data class RegisterResponse (
    val status: Boolean,
    val token: String,
    val user: UserInfo
)

data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    val role: String
)

enum class UniYear {
    FIRST_YEAR,
    SECOND_YEAR,
    THIRD_YEAR,
    FOURTH_YEAR
}

enum class FocusArea {
    COMMON,
    SOFTWARE,
    NETWORKING,
    MULTIMEDIA
}