package com.example.rentago.Response

data class ItemResponse(
    val name: String,
    val description: String,
    val price: Number,
    val images: String  // List of image URLs
)
