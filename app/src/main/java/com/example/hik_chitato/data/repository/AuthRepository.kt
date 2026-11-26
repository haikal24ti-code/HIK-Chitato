package com.example.hik_chitato.data.repository

import com.example.hik_chitato.data.SupabaseClientProvider
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    private val auth = SupabaseClientProvider.auth

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        birthDate: String,
        gender: String,
        username: String
    ): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("full_name", fullName)
                    put("email", email)
                    put("birth_date", birthDate)
                    put("gender", gender)
                    put("username", username)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentSessionOrNull()?.user?.id
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }
}
