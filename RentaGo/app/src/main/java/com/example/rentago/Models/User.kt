package com.example.rentago.Models

data class User(
    val username: String,
    val email: String,
    val fullname: String,
    val avatar: String,
    val listeditems: List<String>?, // List of Item IDs
    val renteditem: List<String>?, // List of Order IDs
    val phonenumber: String,
    val accessToken: String?, // Token if included in API response
    val refreshToken: String? // Token if included in API response
)

