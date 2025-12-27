package com.example.japuraroutef.api

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse
import com.example.japuraroutef.model.LoginRequest
import com.example.japuraroutef.model.LoginResponse
import com.example.japuraroutef.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.japuraroutef.remote.AuthInterceptor
import com.example.japuraroutef.remote.UnauthorizedInterceptor
import com.example.japuraroutef.local.TokenManager
import com.example.japuraroutef.model.TimetableResponse
import com.example.japuraroutef.utils.AuthStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


interface ApiService {
    @POST("/api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("/api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    @GET("/api/timetables/year/{uniYear}")
    suspend fun getTimetableByYear(@Path("uniYear") uniYear: String): TimetableResponse

    // Module endpoints
    @GET("/api/modules")
    suspend fun getAllModules(): ModulesApiResponse

    // GPA endpoints
    @POST("/api/student/semester-gpa")
    suspend fun createOrUpdateSemesterGpa(@Body request: CreateSemesterGpaRequest): GpaApiResponse<SemesterGpaResponse>

    @GET("/api/student/semester-gpa")
    suspend fun getAllSemesterGpas(): GpaApiResponse<List<SemesterGpaResponse>>

    @GET("/api/student/semester-gpa/semester/{semesterId}")
    suspend fun getSemesterGpa(@Path("semesterId") semesterId: String): GpaApiResponse<SemesterGpaResponse>

    @GET("/api/student/semester-gpa/cgpa")
    suspend fun getOverallCgpa(): GpaApiResponse<CgpaResponse>

    @GET("/api/student/semester-gpa/batch-average")
    suspend fun getBatchAverage(): GpaApiResponse<BatchAverageResponse>

    @DELETE("/api/student/semester-gpa/semester/{semesterId}")
    suspend fun deleteSemesterGpa(@Path("semesterId") semesterId: String): GpaApiResponse<Unit>

    // Place endpoints
    @GET("/api/places")
    suspend fun getAllPlaces(): PlaceApiResponse<List<PlaceResponseDto>>

    @GET("/api/places/{id}")
    suspend fun getPlaceById(@Path("id") placeId: String): PlaceApiResponse<PlaceResponseDto>

    @GET("/api/places/search")
    suspend fun searchPlaces(@Query("query") query: String): PlaceApiResponse<List<PlaceResponseDto>>

    @GET("/api/places/search/tag")
    suspend fun searchPlacesByTag(@Query("tag") tag: String): PlaceApiResponse<List<PlaceResponseDto>>

    @GET("/api/places/search/name")
    suspend fun searchPlacesByName(@Query("name") name: String): PlaceApiResponse<List<PlaceResponseDto>>

    companion object {

        private const val BASE_URL = "http://139.59.5.106:8080/"

        private fun getOkHttpClient(context : Context): OkHttpClient{

            val loggingInterceptor = HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            }

            val tokenManager = TokenManager.getInstance(context)

            // Interceptor to handle 401/403 responses
            val unauthorizedInterceptor = UnauthorizedInterceptor(
                tokenManager = tokenManager,
                onUnauthorized = {
                    // Trigger logout event when unauthorized
                    CoroutineScope(Dispatchers.Main).launch {
                        AuthStateManager.triggerLogout()
                    }
                }
            )

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(unauthorizedInterceptor) // Add unauthorized handler
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        fun create(context: Context): ApiService{

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)

        }


    }
}