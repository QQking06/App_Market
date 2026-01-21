package com.hieuwu.groceriesstore.data.network.dto

import com.squareup.moshi.Json

// Versi aman (tanpa @JsonClass) karena kita pakai KotlinJsonAdapterFactory
data class ProductDto(
    @Json(name = "id") val productId: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "price") val price: Double,
    @Json(name = "image") val image: String,
    @Json(name = "category") val category: String,
    @Json(name = "nutrition") val nutrition: String? = null
)