package com.example.hik_chitato

data class SalonService(
    val id: Int,
    val dbId: Long? = null,
    val name: String,
    val description: String,
    val price: String,
    val iconResId: Int
)
