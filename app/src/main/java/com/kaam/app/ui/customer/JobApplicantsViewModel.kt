package com.kaam.app.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.JobApplication
import com.kaam.app.data.model.JobApplicationStatus
import com.kaam.app.data.model.JobRequestStatus
import com.kaam.app.data.model.Profile
import com.kaam.app.data.repository.JobRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApplicantWithProfile(
    val application: JobApplication,
    val profile: Profile,
)

data class JobApplicantsUiState(
    val applicants: List<ApplicantWithProfile> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class JobApplicantsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val jobRequestId: String = savedStateHandle["jobRequestId"] ?: ""

    private val _uiState = MutableStateFlow(JobApplicantsUiState())
    val uiState: StateFlow<JobApplicantsUiState> = _uiState

    init {
        loadApplicants()
    }

    fun loadApplicants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val applications = jobRepository.getApplicationsForJobRequest(jobRequestId)
            val applicants = applications.mapNotNull { app ->
                val profile = profileRepository.getProfile(app.workerId) ?: return@mapNotNull null
                ApplicantWithProfile(application = app, profile = profile)
            }
            _uiState.value = JobApplicantsUiState(applicants = applicants, isLoading = false)
        }
    }

    fun acceptApplication(applicationId: String) {
        viewModelScope.launch {
            jobRepository.updateApplicationStatus(applicationId, JobApplicationStatus.ACCEPTED)
            jobRepository.updateJobRequestStatus(jobRequestId, JobRequestStatus.FILLED)
            loadApplicants()
        }
    }

    fun rejectApplication(applicationId: String) {
        viewModelScope.launch {
            jobRepository.updateApplicationStatus(applicationId, JobApplicationStatus.REJECTED)
            loadApplicants()
        }
    }
}
