package com.kaam.app.ui.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.JobApplication
import com.kaam.app.data.model.JobRequest
import com.kaam.app.data.model.ServiceType
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.JobRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobBoardUiState(
    val jobs: List<JobRequest> = emptyList(),
    val filterService: ServiceType? = null,
    val isLoading: Boolean = true,
    val applyingToJobId: String? = null,
    val proposedRate: String = "",
    val applyMessage: String = "",
    val error: String? = null,
)

@HiltViewModel
class JobBoardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val jobRepository: JobRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobBoardUiState())
    val uiState: StateFlow<JobBoardUiState> = _uiState

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userId = authRepository.currentUserId ?: return@launch
            val profile = profileRepository.getProfile(userId)
            val jobs = jobRepository.getOpenJobRequests(city = profile?.city)
            val filtered = _uiState.value.filterService?.let { filter ->
                jobs.filter { it.service == filter }
            } ?: jobs
            _uiState.value = _uiState.value.copy(jobs = filtered, isLoading = false)
        }
    }

    fun setFilterService(service: ServiceType?) {
        _uiState.value = _uiState.value.copy(filterService = service)
        loadJobs()
    }

    fun startApply(jobId: String) {
        _uiState.value = _uiState.value.copy(applyingToJobId = jobId, proposedRate = "", applyMessage = "", error = null)
    }

    fun cancelApply() {
        _uiState.value = _uiState.value.copy(applyingToJobId = null)
    }

    fun setProposedRate(rate: String) {
        _uiState.value = _uiState.value.copy(proposedRate = rate)
    }

    fun setApplyMessage(message: String) {
        _uiState.value = _uiState.value.copy(applyMessage = message)
    }

    fun submitApplication() {
        viewModelScope.launch {
            val state = _uiState.value
            val jobId = state.applyingToJobId ?: return@launch
            val rate = state.proposedRate.toIntOrNull()
            if (rate == null) {
                _uiState.value = _uiState.value.copy(error = "Enter a valid rate")
                return@launch
            }
            try {
                val userId = authRepository.currentUserId!!
                val application = JobApplication(
                    jobRequestId = jobId,
                    workerId = userId,
                    proposedRate = rate,
                    message = state.applyMessage.ifBlank { null },
                )
                jobRepository.createJobApplication(application)
                _uiState.value = _uiState.value.copy(applyingToJobId = null, error = null)
                loadJobs()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to apply")
            }
        }
    }
}
