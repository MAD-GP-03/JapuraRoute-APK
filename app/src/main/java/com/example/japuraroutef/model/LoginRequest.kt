package com.example.japuraroutef.model

data class LoginRequest (
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Boolean,
    val message: String?,
    val token: String?,
    val user: User?
)

data class User(
    val id: String,
    val email: String,
    val name: String?
)