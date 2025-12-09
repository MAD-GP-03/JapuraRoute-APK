package com.example.japuraroutef.repository

import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import android.util.Log

class AuthRepository {

    private val apiService = ApiService.create()

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepository", "Making registration API call...")
                val response = apiService.registerUser(request)
                Log.d("AuthRepository", "Registration API call successful")
                Result.success(response)
            } catch (e: HttpException) {
                // Capture detailed HTTP error information
                val errorBody = try {
                    e.response()?.errorBody()?.string()
                } catch (ex: Exception) {
                    "Could not read error body: ${ex.message}"
                }

                val httpCode = e.code()
                val httpMessage = e.message()
                val requestUrl = e.response()?.raw()?.request?.url?.toString() ?: "Unknown URL"
                val errorMessage = "HTTP $httpCode Error: $httpMessage\nResponse Body: $errorBody"

                Log.e("AuthRepository", "=== HTTP Error Details ===")
                Log.e("AuthRepository", "Status Code: $httpCode")
                Log.e("AuthRepository", "Status Message: $httpMessage")
                Log.e("AuthRepository", "Request URL: $requestUrl")
                Log.e("AuthRepository", "Error Body: $errorBody")
                Log.e("AuthRepository", "========================")

                Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
                Log.e("AuthRepository", "Network/Other error: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

}