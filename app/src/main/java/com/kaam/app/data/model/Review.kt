package com.kaam.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String? = null,
    @SerialName("booking_id") val bookingId: String,
    @SerialName("reviewer_id") val reviewerId: String,
    @SerialName("worker_id") val workerId: String,
    val rating: Int,
    val comment: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
