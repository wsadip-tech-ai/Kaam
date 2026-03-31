package com.kaam.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BookingStatus {
    @SerialName("pending") PENDING,
    @SerialName("accepted") ACCEPTED,
    @SerialName("declined") DECLINED,
    @SerialName("completed") COMPLETED,
    @SerialName("cancelled") CANCELLED,
}

@Serializable
enum class BookingType {
    @SerialName("direct") DIRECT,
    @SerialName("request") REQUEST,
}

@Serializable
data class Booking(
    val id: String? = null,
    @SerialName("customer_id") val customerId: String,
    @SerialName("worker_id") val workerId: String? = null,
    val type: BookingType,
    val service: ServiceType,
    val date: String,
    @SerialName("time_slot") val timeSlot: String,
    val address: String,
    val notes: String? = null,
    val status: BookingStatus = BookingStatus.PENDING,
    @SerialName("created_at") val createdAt: String? = null,
)
