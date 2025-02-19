package com.example.rentago.Models

data class Item(
    val name: String,
    val description: String,
    val category: String="",          // "Electronics", "Furniture", etc.
    val owner: User,             // Owner's User ID
    val price: Number,
    val available: Boolean = true, // Default is true
    val images: List<String> = emptyList() // List of image URLs
)