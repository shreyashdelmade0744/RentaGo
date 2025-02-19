package com.example.rentago.Retrofit.Registration

import com.example.rentago.Requests.LoginRequest
import com.example.rentago.Response.LoginResponse
import com.example.rentago.Response.RegistrationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("register")
    fun registerUser(
        @Part("fullname") fullname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("username") username: RequestBody,
        @Part("phonenumber") phonenumber: RequestBody,
        @Part("password") password: RequestBody,
        @Part avatar: MultipartBody.Part // Image file
    ): Call<RegistrationResponse>


    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("refresh-token")
    fun refreshToken(@Header("Authorization") refreshToken: String): Call<LoginResponse>

}