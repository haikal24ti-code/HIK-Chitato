package com.example.hik_chitato.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val email: String,
    @SerialName("birth_date") val birthDate: String? = null,
    val gender: String? = null,
    val username: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
