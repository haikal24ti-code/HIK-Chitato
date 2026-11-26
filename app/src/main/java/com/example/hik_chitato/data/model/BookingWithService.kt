package com.example.hik_chitato.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingWithService(
    val id: Long? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("service_id") val serviceId: Long,
    @SerialName("booking_date") val bookingDate: String,
    @SerialName("booking_time") val bookingTime: String,
    val status: String = "Pending",
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("salon_services") val salonService: SalonService? = null
)
