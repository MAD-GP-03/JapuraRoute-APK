package com.example.japuraroutef.repository

import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    private val apiService = ApiService.create()

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.registerUser(request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


}