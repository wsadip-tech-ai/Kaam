package com.kaam.app.ui.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Booking
import com.kaam.app.data.model.BookingStatus
import com.kaam.app.data.model.WorkerProfile
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.BookingRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerDashboardState(
    val workerProfile: WorkerProfile? = null,
    val pendingBookings: List<Booking> = emptyList(),
    val activeBookings: List<Booking> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val bookingRepository: BookingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerDashboardState())
    val uiState: StateFlow<WorkerDashboardState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val wp = profileRepository.getWorkerProfile(userId)
            val bookings = bookingRepository.getWorkerBookings(userId)
            _uiState.value = WorkerDashboardState(
                workerProfile = wp,
                pendingBookings = bookings.filter { it.status == BookingStatus.PENDING },
                activeBookings = bookings.filter { it.status == BookingStatus.ACCEPTED },
                isLoading = false,
            )
        }
    }

    fun toggleAvailability() {
        viewModelScope.launch {
            val wp = _uiState.value.workerProfile ?: return@launch
            val updated = wp.copy(isAvailable = !wp.isAvailable)
            profileRepository.updateWorkerProfile(updated)
            _uiState.value = _uiState.value.copy(workerProfile = updated)
        }
    }
}
