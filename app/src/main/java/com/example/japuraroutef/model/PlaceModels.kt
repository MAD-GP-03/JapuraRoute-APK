package com.example.japuraroutef.model

import com.google.gson.annotations.SerializedName

// API Response wrapper
data class PlaceApiResponse<T>(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T?,
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("statusCode")
    val statusCode: Int?
) {
    // Helper property to check if response is successful
    val isSuccessful: Boolean
        get() = status || success == true
}

// Place Response DTO
data class PlaceResponseDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortDescription")
    val shortDescription: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("location")
    val location: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("contactPhone")
    val contactPhone: String?,
    @SerializedName("websiteUrl")
    val websiteUrl: String?,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("operatingHours")
    val operatingHours: List<OperatingHourDto>,
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("ratingCount")
    val ratingCount: Int,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("updatedBy")
    val updatedBy: String?
)

// Operating Hour DTO
data class OperatingHourDto(
    @SerializedName("day")
    val day: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    @SerializedName("startTime")
    val startTime: String?, // HH:mm format
    @SerializedName("endTime")
    val endTime: String?, // HH:mm format
    @SerializedName("note")
    val note: String?
)

// Place Category for filtering
enum class PlaceCategory(val displayName: String, val tag: String) {
    ALL("All", ""),
    STUDY("Study", "Study"),
    FOOD("Food", "Food"),
    ADMIN("Admin", "Administrative"),
    HEALTH("Health", "Health"),
    SPORTS("Sports", "Sports");

    companion object {
        fun fromTag(tag: String): PlaceCategory {
            return entries.find { it.tag.equals(tag, ignoreCase = true) } ?: ALL
        }
    }
}

