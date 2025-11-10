package com.monaco.app.data.models

data class Product(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val imagePath: String? = null
)
