package com.kaam.app.data.repository

import com.kaam.app.data.model.Profile
import com.kaam.app.data.model.WorkerProfile
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val postgrest: Postgrest,
) {

    // ---------- Profile ----------

    suspend fun getProfile(userId: String): Profile? {
        return postgrest.from("profiles")
            .select {
                filter { eq("id", userId) }
            }
            .decodeSingleOrNull<Profile>()
    }

    suspend fun createProfile(profile: Profile): Profile {
        return postgrest.from("profiles")
            .insert(profile) {
                select()
            }
            .decodeSingle<Profile>()
    }

    suspend fun updateProfile(profile: Profile): Profile {
        return postgrest.from("profiles")
            .update(profile) {
                select()
                filter { eq("id", profile.id) }
            }
            .decodeSingle<Profile>()
    }

    suspend fun deleteProfile(userId: String) {
        postgrest.from("profiles")
            .delete {
                filter { eq("id", userId) }
            }
    }

    // ---------- WorkerProfile ----------

    suspend fun getWorkerProfile(workerId: String): WorkerProfile? {
        return postgrest.from("worker_profiles")
            .select {
                filter { eq("id", workerId) }
            }
            .decodeSingleOrNull<WorkerProfile>()
    }

    suspend fun createWorkerProfile(workerProfile: WorkerProfile): WorkerProfile {
        return postgrest.from("worker_profiles")
            .insert(workerProfile) {
                select()
            }
            .decodeSingle<WorkerProfile>()
    }

    suspend fun updateWorkerProfile(workerProfile: WorkerProfile): WorkerProfile {
        return postgrest.from("worker_profiles")
            .update(workerProfile) {
                select()
                filter { eq("id", workerProfile.id) }
            }
            .decodeSingle<WorkerProfile>()
    }

    suspend fun upsertWorkerProfile(workerProfile: WorkerProfile): WorkerProfile {
        return postgrest.from("worker_profiles")
            .upsert(workerProfile) {
                select()
            }
            .decodeSingle<WorkerProfile>()
    }
}
