package com.example.japuraroutef.remote

import com.example.japuraroutef.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val TokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) : Response {

        val origialRequest = chain.request()
        val token = TokenManager.getToken()

        return if (token != null) {
            val newRequest = origialRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(origialRequest)
        }

    }

}