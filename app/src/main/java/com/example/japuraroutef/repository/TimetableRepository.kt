package com.example.japuraroutef.repository

import android.content.Context
import android.util.Log
import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.TimetableData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Repository for handling timetable-related API calls
 */
class TimetableRepository(private val context: Context) {

    private val apiService = ApiService.create(context)
    private val TAG = "TimetableRepository"

    /**
     * Fetch timetable for a specific university year
     * @param uniYear The university year (e.g., "FIRST_YEAR", "SECOND_YEAR", etc.)
     * @return TimetableData if successful, null otherwise
     */
    suspend fun getTimetableByYear(uniYear: String): Result<TimetableData?> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching timetable for year: $uniYear")
                val response = apiService.getTimetableByYear(uniYear)

                if (response.status && response.data != null) {
                    Log.d(TAG, "Timetable fetched successfully")
                    Result.success(response.data)
                } else {
                    Log.e(TAG, "Timetable fetch failed: ${response.message}")
                    Result.failure(Exception(response.message ?: "Failed to fetch timetable"))
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(TAG, "=== HTTP Error Details ===")
                Log.e(TAG, "Status Code: ${e.code()}")
                Log.e(TAG, "Error Body: $errorBody")
                Log.e(TAG, "========================")
                Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching timetable", e)
                Result.failure(e)
            }
        }
    }
}

