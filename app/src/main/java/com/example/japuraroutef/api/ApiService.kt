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
import com.example.japuraroutef.local.TokenManager
import com.example.japuraroutef.model.TimetableResponse
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

    companion object {

        private const val BASE_URL = "http://139.59.5.106:8080/"

        private fun getOkHttpClient(context : Context): OkHttpClient{

            val loggingInterceptor = HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AuthInterceptor(TokenManager(context)))
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