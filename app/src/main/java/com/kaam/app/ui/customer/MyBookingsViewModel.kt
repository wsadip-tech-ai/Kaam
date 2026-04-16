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

data class MyBookingsUiState(
    val activeBookings: List<Booking> = emptyList(),
    val pastBookings: List<Booking> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyBookingsUiState())
    val uiState: StateFlow<MyBookingsUiState> = _uiState

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val bookings = bookingRepository.getCustomerBookings(userId)
            _uiState.value = MyBookingsUiState(
                activeBookings = bookings.filter {
                    it.status == BookingStatus.PENDING || it.status == BookingStatus.ACCEPTED
                },
                pastBookings = bookings.filter {
                    it.status == BookingStatus.COMPLETED || it.status == BookingStatus.CANCELLED || it.status == BookingStatus.DECLINED
                },
                isLoading = false,
            )
        }
    }
}
