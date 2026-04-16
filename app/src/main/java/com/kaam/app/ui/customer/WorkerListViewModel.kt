package com.kaam.app.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.ServiceType
import com.kaam.app.data.model.VerificationStatus
import com.kaam.app.data.model.WorkerWithProfile
import com.kaam.app.data.repository.ProfileRepository
import com.kaam.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val service: String = savedStateHandle["service"] ?: "cleaning"

    private val _workers = MutableStateFlow<List<WorkerWithProfile>>(emptyList())
    val workers: StateFlow<List<WorkerWithProfile>> = _workers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadWorkers()
    }

    fun loadWorkers() {
        viewModelScope.launch {
            _isLoading.value = true
            val profiles = profileRepository.getVerifiedWorkersByService(service)
            val workersWithProfiles = profiles.mapNotNull { profile ->
                val wp = profileRepository.getWorkerProfile(profile.id) ?: return@mapNotNull null
                if (wp.verificationStatus != VerificationStatus.VERIFIED) return@mapNotNull null
                val targetService = ServiceType.entries.find { it.name.equals(service, ignoreCase = true) }
                if (targetService != null && targetService !in wp.services) return@mapNotNull null
                val reviews = reviewRepository.getWorkerReviews(profile.id)
                val avg = if (reviews.isEmpty()) null else reviews.map { it.rating }.average()
                WorkerWithProfile(
                    profile = profile,
                    workerProfile = wp,
                    averageRating = avg,
                    reviewCount = reviews.size,
                )
            }
            _workers.value = workersWithProfiles
            _isLoading.value = false
        }
    }
}
