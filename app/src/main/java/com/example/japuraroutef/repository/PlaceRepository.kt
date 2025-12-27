package com.example.japuraroutef.repository

import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.PlaceResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaceRepository(private val apiService: ApiService) {

    suspend fun getAllPlaces(): Result<List<PlaceResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllPlaces()
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch places"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlaceById(placeId: String): Result<PlaceResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPlaceById(placeId)
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch place details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchPlaces(query: String): Result<List<PlaceResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchPlaces(query)
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchPlacesByTag(tag: String): Result<List<PlaceResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchPlacesByTag(tag)
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Search by tag failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchPlacesByName(name: String): Result<List<PlaceResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchPlacesByName(name)
            if (response.isSuccessful && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Search by name failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

