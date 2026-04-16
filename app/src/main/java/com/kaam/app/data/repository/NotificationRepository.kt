package com.kaam.app.data.repository

import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val postgrest: Postgrest,
) {
    suspend fun saveFcmToken(userId: String, token: String) {
        postgrest.from("profiles")
            .update({ set("fcm_token", token) }) {
                filter { eq("id", userId) }
            }
    }
}
