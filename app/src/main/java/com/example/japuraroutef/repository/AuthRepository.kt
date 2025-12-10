package com.example.japuraroutef.repository

import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import android.util.Log
import com.example.japuraroutef.local.TokenManager
import com.example.japuraroutef.model.LoginRequest
import com.example.japuraroutef.model.LoginResponse

class AuthRepository (
    private val apiService: ApiService,
    private val tokenManager: TokenManager
){

    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Making login API call...")
                val response = apiService.loginUser(request)

                if (response.status && response.token != null) {
                    // Save token
                    tokenManager.saveToken(response.token)

                    // Save user data if available
                    response.user?.let { user ->
                        tokenManager.saveUser(user)
                    }

                    Log.d(TAG, "Login successful, token and user data saved.")
                    Result.success(response)
                } else {
                    val errorMessage = response.message ?: "Login failed"
                    Log.e(TAG, "Login failed: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                // Capture detailed HTTP error information
                val errorBody = try {
                    e.response()?.errorBody()?.string() ?: ""
                } catch (ex: Exception) {
                    ""
                }

                val httpCode = e.code()

                // Try to parse error message from JSON response
                val errorMessage = try {
                    when {
                        // Format 1: {"status": false, "error": "Invalid email or password"}
                        errorBody.contains("\"error\"") && errorBody.contains("\"status\"") -> {
                            val regex = """"error"\s*:\s*"([^"]*)"""".toRegex()
                            val match = regex.find(errorBody)
                            match?.groupValues?.get(1) ?: "Login failed"
                        }
                        // Format 2: {"timestamp": "...", "message": "Invalid input data", "errors": {"email": "Email must be valid"}}
                        errorBody.contains("\"errors\"") -> {
                            // Try to extract first field error
                            val fieldErrorRegex = """"(\w+)"\s*:\s*"([^"]*)"""".toRegex()
                            val matches = fieldErrorRegex.findAll(errorBody).toList()
                            // Find the first error message inside "errors" object
                            val errorMatch = matches.find { it.groupValues[1] != "timestamp" && it.groupValues[1] != "error" && it.groupValues[1] != "message" && it.groupValues[1] != "status" }
                            errorMatch?.groupValues?.get(2) ?: "Invalid input data"
                        }
                        // Format 3: {"message": "Some error message"}
                        errorBody.contains("\"message\"") -> {
                            val regex = """"message"\s*:\s*"([^"]*)"""".toRegex()
                            val match = regex.find(errorBody)
                            match?.groupValues?.get(1) ?: "Login failed"
                        }
                        else -> "Login failed"
                    }
                } catch (ex: Exception) {
                    "Login failed"
                }

                Log.e(TAG, "=== Login HTTP Error Details ===")
                Log.e(TAG, "Status Code: $httpCode")
                Log.e(TAG, "Error Body: $errorBody")
                Log.e(TAG, "Extracted Error: $errorMessage")
                Log.e(TAG, "================================")

                Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
                Log.e(TAG, "Login error: ${e.message}", e)
                Result.failure(Exception(e.message ?: "Network error occurred"))
            }
        }

    }

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
                    e.response()?.errorBody()?.string() ?: ""
                } catch (ex: Exception) {
                    ""
                }

                val httpCode = e.code()
                val httpMessage = e.message()
                val requestUrl = e.response()?.raw()?.request?.url?.toString() ?: "Unknown URL"

                // Try to parse error message from JSON response
                val errorMessage = try {
                    when {
                        // Format 1: {"status": false, "error": "Email already exists"}
                        errorBody.contains("\"error\"") && errorBody.contains("\"status\"") -> {
                            val regex = """"error"\s*:\s*"([^"]*)"""".toRegex()
                            val match = regex.find(errorBody)
                            match?.groupValues?.get(1) ?: "Registration failed"
                        }
                        // Format 2: {"timestamp": "...", "message": "Invalid input data", "errors": {"email": "Email must be valid"}}
                        errorBody.contains("\"errors\"") -> {
                            // Try to extract first field error
                            val fieldErrorRegex = """"(\w+)"\s*:\s*"([^"]*)"""".toRegex()
                            val matches = fieldErrorRegex.findAll(errorBody).toList()
                            // Find the first error message inside "errors" object
                            val errorMatch = matches.find { it.groupValues[1] != "timestamp" && it.groupValues[1] != "error" && it.groupValues[1] != "message" && it.groupValues[1] != "status" }
                            errorMatch?.groupValues?.get(2) ?: "Invalid input data"
                        }
                        // Format 3: {"message": "Some error message"}
                        errorBody.contains("\"message\"") -> {
                            val regex = """"message"\s*:\s*"([^"]*)"""".toRegex()
                            val match = regex.find(errorBody)
                            match?.groupValues?.get(1) ?: "Registration failed"
                        }
                        else -> "Registration failed"
                    }
                } catch (ex: Exception) {
                    "Registration failed"
                }

                Log.e(TAG, "=== Registration HTTP Error Details ===")
                Log.e(TAG, "Status Code: $httpCode")
                Log.e(TAG, "Status Message: $httpMessage")
                Log.e(TAG, "Request URL: $requestUrl")
                Log.e(TAG, "Error Body: $errorBody")
                Log.e(TAG, "Extracted Error: $errorMessage")
                Log.e(TAG, "=======================================")

                Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
                Log.e(TAG, "Network/Other error: ${e.message}", e)
                Result.failure(Exception(e.message ?: "Network error occurred"))
            }
        }
    }

}