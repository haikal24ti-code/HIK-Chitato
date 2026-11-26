package com.example.hik_chitato.data.repository

import com.example.hik_chitato.data.SupabaseClientProvider
import com.example.hik_chitato.data.model.Booking

class BookingRepository {

    private val postgrest = SupabaseClientProvider.postgrest
    private val tableName = "bookings"

    suspend fun getBookingsByUser(userId: String): Result<List<Booking>> {
        return try {
            val list = postgrest[tableName]
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Booking>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBooking(
        userId: String,
        serviceId: Long,
        bookingDate: String,
        bookingTime: String,
        notes: String? = null
    ): Result<Unit> {
        return try {
            val booking = Booking(
                userId = userId,
                serviceId = serviceId,
                bookingDate = bookingDate,
                bookingTime = bookingTime,
                status = "Pending",
                notes = notes
            )
            postgrest[tableName].insert(booking)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBooking(
        id: Long,
        serviceId: Long,
        bookingDate: String,
        bookingTime: String,
        notes: String?
    ): Result<Unit> {
        return try {
            postgrest[tableName].update({
                set("service_id", serviceId)
                set("booking_date", bookingDate)
                set("booking_time", bookingTime)
                set("notes", notes)
            }) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBooking(id: Long): Result<Unit> {
        return try {
            postgrest[tableName].delete {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
