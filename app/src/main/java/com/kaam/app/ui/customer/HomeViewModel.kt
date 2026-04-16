package com.kaam.app.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Booking
import com.kaam.app.data.model.BookingStatus
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val activeBookings: List<Booking> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val bookings = bookingRepository.getCustomerBookings(userId)
            _uiState.value = HomeUiState(
                activeBookings = bookings.filter {
                    it.status == BookingStatus.PENDING || it.status == BookingStatus.ACCEPTED
                },
                isLoading = false,
            )
        }
    }
}
