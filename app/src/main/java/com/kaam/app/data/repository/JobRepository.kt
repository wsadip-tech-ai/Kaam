package com.kaam.app.data.repository

import com.kaam.app.data.model.JobApplication
import com.kaam.app.data.model.JobApplicationStatus
import com.kaam.app.data.model.JobRequest
import com.kaam.app.data.model.JobRequestStatus
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val postgrest: Postgrest,
) {

    // ---------- JobRequest ----------

    suspend fun getOpenJobRequests(city: String? = null): List<JobRequest> {
        return postgrest.from("job_requests")
            .select {
                filter {
                    eq("status", JobRequestStatus.OPEN)
                    if (city != null) eq("city", city)
                }
            }
            .decodeList<JobRequest>()
    }

    suspend fun getCustomerJobRequests(customerId: String): List<JobRequest> {
        return postgrest.from("job_requests")
            .select {
                filter { eq("customer_id", customerId) }
            }
            .decodeList<JobRequest>()
    }

    suspend fun getJobRequest(jobRequestId: String): JobRequest? {
        return postgrest.from("job_requests")
            .select {
                filter { eq("id", jobRequestId) }
            }
            .decodeSingleOrNull<JobRequest>()
    }

    suspend fun createJobRequest(jobRequest: JobRequest): JobRequest {
        return postgrest.from("job_requests")
            .insert(jobRequest) {
                select()
            }
            .decodeSingle<JobRequest>()
    }

    suspend fun updateJobRequestStatus(jobRequestId: String, status: JobRequestStatus): JobRequest {
        return postgrest.from("job_requests")
            .update({ set("status", status) }) {
                select()
                filter { eq("id", jobRequestId) }
            }
            .decodeSingle<JobRequest>()
    }

    suspend fun deleteJobRequest(jobRequestId: String) {
        postgrest.from("job_requests")
            .delete {
                filter { eq("id", jobRequestId) }
            }
    }

    // ---------- JobApplication ----------

    suspend fun getApplicationsForJobRequest(jobRequestId: String): List<JobApplication> {
        return postgrest.from("job_applications")
            .select {
                filter { eq("job_request_id", jobRequestId) }
            }
            .decodeList<JobApplication>()
    }

    suspend fun getWorkerApplications(workerId: String): List<JobApplication> {
        return postgrest.from("job_applications")
            .select {
                filter { eq("worker_id", workerId) }
            }
            .decodeList<JobApplication>()
    }

    suspend fun createJobApplication(application: JobApplication): JobApplication {
        return postgrest.from("job_applications")
            .insert(application) {
                select()
            }
            .decodeSingle<JobApplication>()
    }

    suspend fun updateApplicationStatus(
        applicationId: String,
        status: JobApplicationStatus,
    ): JobApplication {
        return postgrest.from("job_applications")
            .update({ set("status", status) }) {
                select()
                filter { eq("id", applicationId) }
            }
            .decodeSingle<JobApplication>()
    }

    suspend fun deleteJobApplication(applicationId: String) {
        postgrest.from("job_applications")
            .delete {
                filter { eq("id", applicationId) }
            }
    }
}
