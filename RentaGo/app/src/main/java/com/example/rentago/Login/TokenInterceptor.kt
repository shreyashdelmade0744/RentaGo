package com.example.rentago.Login

import android.content.Context
import com.example.rentago.Retrofit.Registration.Login.RetrofitLoginClient
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(context: Context) : Interceptor {
    private val tokenManager = TokenManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val accessToken = tokenManager.getAccessToken()
        if (!accessToken.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $accessToken")
        }

        val response = chain.proceed(request.build())

        if (response.code() == 401) {
            synchronized(this) {
                val newAccessToken = refreshAccessToken()
                if (newAccessToken != null) {
                    tokenManager.saveTokens(newAccessToken, tokenManager.getRefreshToken() ?: "")
                    return chain.proceed(
                        request.removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()
                    )
                }
            }
        }

        return response
    }

    private fun refreshAccessToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        val response = RetrofitLoginClient.instance.refreshToken(refreshToken).execute()

        return if (response.isSuccessful) {
            response.body()?.accessToken
        } else null
    }
}