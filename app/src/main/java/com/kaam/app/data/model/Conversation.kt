package com.kaam.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: String? = null,
    @SerialName("booking_id") val bookingId: String? = null,
    @SerialName("job_request_id") val jobRequestId: String? = null,
    @SerialName("customer_id") val customerId: String,
    @SerialName("worker_id") val workerId: String,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class Message(
    val id: String? = null,
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("sender_id") val senderId: String,
    val content: String,
    @SerialName("sent_at") val sentAt: String? = null,
    @SerialName("read_at") val readAt: String? = null,
)
