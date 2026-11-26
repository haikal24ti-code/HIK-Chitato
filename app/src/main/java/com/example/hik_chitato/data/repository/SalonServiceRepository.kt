package com.example.hik_chitato.data.repository

import com.example.hik_chitato.data.SupabaseClientProvider
import com.example.hik_chitato.data.model.SalonService
import io.github.jan.supabase.postgrest.postgrest

class SalonServiceRepository {

    private val postgrest = SupabaseClientProvider.postgrest
    private val tableName = "salon_services"

    suspend fun getAllServices(): Result<List<SalonService>> {
        return try {
            val list = postgrest[tableName].select().decodeList<SalonService>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createService(name: String, description: String, price: Double): Result<Unit> {
        return try {
            val newService = SalonService(
                name = name,
                description = description,
                price = price
            )
            postgrest[tableName].insert(newService)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateService(id: Long, name: String, description: String, price: Double): Result<Unit> {
        return try {
            postgrest[tableName].update({
                set("name", name)
                set("description", description)
                set("price", price)
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

    suspend fun deleteService(id: Long): Result<Unit> {
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
