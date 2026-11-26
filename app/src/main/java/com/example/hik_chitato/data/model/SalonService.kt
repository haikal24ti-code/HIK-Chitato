package com.example.hik_chitato.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalonService(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: Double,
    @SerialName("created_at") val createdAt: String? = null
)
