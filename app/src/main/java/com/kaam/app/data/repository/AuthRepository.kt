package com.kaam.app.data.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.user.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: Auth,
) {

    val currentUserId: String?
        get() = auth.currentSessionOrNull()?.user?.id

    suspend fun sendOtp(phone: String) {
        auth.signInWith(OTP) {
            this.phone = phone
        }
    }

    suspend fun verifyOtp(phone: String, token: String) {
        auth.verifyPhoneOtp(
            type = OtpType.Phone.SMS,
            phone = phone,
            token = token,
        )
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): UserInfo? {
        return auth.currentSessionOrNull()?.user
    }
}
