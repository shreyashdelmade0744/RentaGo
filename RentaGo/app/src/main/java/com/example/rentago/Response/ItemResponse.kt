package com.example.rentago.Response

import com.example.rentago.Models.Item

data class ItemResponse(
    val success: Boolean,
    val item:Item,
    val message:String
)
