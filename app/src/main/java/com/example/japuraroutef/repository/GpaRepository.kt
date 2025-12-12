package com.example.japuraroutef.repository

import android.util.Log
import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GpaRepository(private val apiService: ApiService) {

    suspend fun getAllModules(): Result<List<Module>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllModules()
                if (response.status && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to fetch modules"))
                }
            } catch (e: HttpException) {
                Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GpaRepository", "Error body: $errorBody")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error fetching modules", e)
                Result.failure(e)
            }
        }
    }

    suspend fun createOrUpdateSemesterGpa(request: CreateSemesterGpaRequest): Result<SemesterGpaResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createOrUpdateSemesterGpa(request)
                if (response.status && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to create/update semester GPA"))
                }
            } catch (e: HttpException) {
                Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GpaRepository", "Error body: $errorBody")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error creating/updating semester GPA", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getAllSemesterGpas(): Result<List<SemesterGpaResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllSemesterGpas()
                if (response.status && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to fetch semester GPAs"))
                }
            } catch (e: HttpException) {
                Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GpaRepository", "Error body: $errorBody")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error fetching semester GPAs", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getSemesterGpa(semesterId: SemesterId): Result<SemesterGpaResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSemesterGpa(semesterId.name)
                if (response.status && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to fetch semester GPA"))
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    Result.failure(Exception("Semester GPA not found"))
                } else {
                    Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("GpaRepository", "Error body: $errorBody")
                    Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error fetching semester GPA", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getOverallCgpa(): Result<CgpaResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOverallCgpa()
                if (response.status && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to fetch CGPA"))
                }
            } catch (e: HttpException) {
                Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GpaRepository", "Error body: $errorBody")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error fetching CGPA", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getBatchAverage(): Result<BatchAverageResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBatchAverage()
                if (response.status && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to fetch batch average"))
                }
            } catch (e: HttpException) {
                Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GpaRepository", "Error body: $errorBody")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error fetching batch average", e)
                Result.failure(e)
            }
        }
    }

    suspend fun deleteSemesterGpa(semesterId: SemesterId): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteSemesterGpa(semesterId.name)
                if (response.status) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to delete semester GPA"))
                }
            } catch (e: HttpException) {
                Log.e("GpaRepository", "HTTP ${e.code()}: ${e.message()}")
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GpaRepository", "Error body: $errorBody")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e("GpaRepository", "Error deleting semester GPA", e)
                Result.failure(e)
            }
        }
    }

    // Local calculation helper
    fun calculateGpa(subjects: List<Subject>): Float {
        if (subjects.isEmpty()) return 0f

        val totalWeightedGpa = subjects.sumOf {
            val gradeValue = Grade.fromString(it.grade)?.gpaValue ?: 0f
            (gradeValue * it.credits).toDouble()
        }
        val totalCredits = subjects.sumOf { it.credits.toDouble() }

        return if (totalCredits > 0) {
            (totalWeightedGpa / totalCredits).toFloat()
        } else {
            0f
        }
    }
}

