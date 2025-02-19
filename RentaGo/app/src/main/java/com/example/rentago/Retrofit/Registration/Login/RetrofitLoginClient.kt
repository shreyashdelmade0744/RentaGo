package com.example.rentago.Retrofit.Registration.Login

import com.example.rentago.Retrofit.Registration.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitLoginClient {
    private const val BASE_URL = "https://rentago-lq51.onrender.com/api/v1/users/" // Change to your backend URL

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}