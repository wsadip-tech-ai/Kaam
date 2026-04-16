package com.kaam.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class PostJobUiState(
    val service: ServiceType = ServiceType.CLEANING,
    val description: String = "",
    val date: String = "",
    val budgetMax: String = "",
    val city: String = "",
    val area: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false,
)

@HiltViewModel
class PostJobViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val jobRepository: JobRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostJobUiState())
    val uiState: StateFlow<PostJobUiState> = _uiState

    init {
        loadUserCity()
    }

    private fun loadUserCity() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val profile = profileRepository.getProfile(userId) ?: return@launch
            _uiState.value = _uiState.value.copy(city = profile.city, area = profile.area ?: "")
        }
    }

    fun setService(service: ServiceType) {
        _uiState.value = _uiState.value.copy(service = service)
    }

    fun setDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun setDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun setBudgetMax(budget: String) {
        _uiState.value = _uiState.value.copy(budgetMax = budget)
    }

    fun submit() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userId = authRepository.currentUserId!!
                val state = _uiState.value
                val jobRequest = JobRequest(
                    customerId = userId,
                    service = state.service,
                    description = state.description,
                    date = state.date,
                    budgetMax = state.budgetMax.toIntOrNull(),
                    city = state.city,
                    area = state.area.ifBlank { null },
                )
                jobRepository.createJobRequest(jobRequest)
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to post job",
                )
            }
        }
    }
}
