package com.kaam.app.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Profile
import com.kaam.app.data.model.Review
import com.kaam.app.data.model.WorkerProfile
import com.kaam.app.data.repository.ProfileRepository
import com.kaam.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerProfileUiState(
    val profile: Profile? = null,
    val workerProfile: WorkerProfile? = null,
    val reviews: List<Review> = emptyList(),
    val averageRating: Double? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val workerId: String = savedStateHandle["workerId"] ?: ""

    private val _uiState = MutableStateFlow(WorkerProfileUiState())
    val uiState: StateFlow<WorkerProfileUiState> = _uiState

    init {
        loadWorkerProfile()
    }

    private fun loadWorkerProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getProfile(workerId)
            val workerProfile = profileRepository.getWorkerProfile(workerId)
            val reviews = reviewRepository.getWorkerReviews(workerId)
            val avg = if (reviews.isEmpty()) null else reviews.map { it.rating }.average()
            _uiState.value = WorkerProfileUiState(
                profile = profile,
                workerProfile = workerProfile,
                reviews = reviews,
                averageRating = avg,
                isLoading = false,
            )
        }
    }
}
