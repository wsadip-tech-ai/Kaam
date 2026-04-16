package com.kaam.app.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Booking
import com.kaam.app.data.model.BookingStatus
import com.kaam.app.data.model.WorkerProfile
import com.kaam.app.data.repository.BookingRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val booking: Booking? = null,
    val workerProfile: WorkerProfile? = null,
    val isLoading: Boolean = true,
    val isPaid: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookingRepository: BookingRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val bookingId: String = savedStateHandle["bookingId"] ?: ""

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState

    init {
        loadPaymentDetails()
    }

    private fun loadPaymentDetails() {
        viewModelScope.launch {
            val booking = bookingRepository.getBooking(bookingId)
            val wp = booking?.workerId?.let { profileRepository.getWorkerProfile(it) }
            _uiState.value = PaymentUiState(
                booking = booking,
                workerProfile = wp,
                isLoading = false,
            )
        }
    }

    fun markAsPaid() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                bookingRepository.updateBookingStatus(bookingId, BookingStatus.COMPLETED)
                _uiState.value = _uiState.value.copy(isLoading = false, isPaid = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update booking",
                )
            }
        }
    }
}
