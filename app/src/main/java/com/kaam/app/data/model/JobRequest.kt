package com.kaam.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class JobRequestStatus {
    @SerialName("open") OPEN,
    @SerialName("filled") FILLED,
    @SerialName("closed") CLOSED,
}

@Serializable
enum class JobApplicationStatus {
    @SerialName("pending") PENDING,
    @SerialName("accepted") ACCEPTED,
    @SerialName("rejected") REJECTED,
}

@Serializable
data class JobRequest(
    val id: String? = null,
    @SerialName("customer_id") val customerId: String,
    val service: ServiceType,
    val description: String,
    val date: String,
    @SerialName("budget_max") val budgetMax: Int? = null,
    val city: String,
    val area: String? = null,
    val status: JobRequestStatus = JobRequestStatus.OPEN,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class JobApplication(
    val id: String? = null,
    @SerialName("job_request_id") val jobRequestId: String,
    @SerialName("worker_id") val workerId: String,
    @SerialName("proposed_rate") val proposedRate: Int,
    val message: String? = null,
    val status: JobApplicationStatus = JobApplicationStatus.PENDING,
    @SerialName("created_at") val createdAt: String? = null,
)
