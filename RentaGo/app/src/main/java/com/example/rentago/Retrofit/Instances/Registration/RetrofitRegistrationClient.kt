package com.example.rentago.Retrofit.Instances.Registration

import com.example.rentago.Retrofit.Instances.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitRegistrationClient {
    private const val BASE_URL = "https://rentago-lq51.onrender.com/api/v1/users/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}