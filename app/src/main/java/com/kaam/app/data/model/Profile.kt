package com.kaam.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("customer") CUSTOMER,
    @SerialName("worker") WORKER,
}

@Serializable
enum class ServiceType {
    @SerialName("cleaning") CLEANING,
    @SerialName("nanny") NANNY,
}

@Serializable
enum class VerificationStatus {
    @SerialName("pending") PENDING,
    @SerialName("verified") VERIFIED,
    @SerialName("rejected") REJECTED,
}

@Serializable
data class Profile(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val phone: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: UserRole,
    val city: String,
    val area: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class WorkerProfile(
    val id: String,
    val bio: String? = null,
    val services: List<ServiceType> = emptyList(),
    @SerialName("hourly_rate") val hourlyRate: Int,
    @SerialName("citizenship_doc_url") val citizenshipDocUrl: String? = null,
    @SerialName("payment_qr_url") val paymentQrUrl: String? = null,
    @SerialName("payment_phone") val paymentPhone: String? = null,
    @SerialName("verification_status") val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("is_available") val isAvailable: Boolean = true,
)

@Serializable
data class WorkerWithProfile(
    val profile: Profile,
    val workerProfile: WorkerProfile,
    val averageRating: Double? = null,
    val reviewCount: Int = 0,
)
