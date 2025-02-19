package com.example.rentago.Response

import com.example.rentago.Models.User

data class LoginResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
