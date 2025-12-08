package com.example.japuraroutef.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.japuraroutef.model.RegisterRequest
import com.example.japuraroutef.model.RegisterResponse

interface ApiService {
    @POST("/api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    companion object {

        private const val BASE_URL = "http://139.59.5.106:8080/"

        fun create(): ApiService{

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)

        }


    }
}