package com.monaco.app.data.models

data class StoreLocation(
    val id: Int = 0,
    val storeName: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double
)
