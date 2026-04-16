package com.kaam.app.ui.worker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Booking
import com.kaam.app.data.model.BookingStatus
import com.kaam.app.data.model.Profile
import com.kaam.app.data.repository.BookingRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingDetailUiState(
    val booking: Booking? = null,
    val customerProfile: Profile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookingRepository: BookingRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val bookingId: String = savedStateHandle["bookingId"] ?: ""

    private val _uiState = MutableStateFlow(BookingDetailUiState())
    val uiState: StateFlow<BookingDetailUiState> = _uiState

    init {
        loadBooking()
    }

    fun loadBooking() {
        viewModelScope.launch {
            val booking = bookingRepository.getBooking(bookingId)
            val customer = booking?.let { profileRepository.getProfile(it.customerId) }
            _uiState.value = BookingDetailUiState(
                booking = booking,
                customerProfile = customer,
                isLoading = false,
            )
        }
    }

    fun acceptBooking() {
        updateStatus(BookingStatus.ACCEPTED)
    }

    fun declineBooking() {
        updateStatus(BookingStatus.DECLINED)
    }

    fun markComplete() {
        updateStatus(BookingStatus.COMPLETED)
    }

    private fun updateStatus(status: BookingStatus) {
        viewModelScope.launch {
            try {
                val updated = bookingRepository.updateBookingStatus(bookingId, status)
                _uiState.value = _uiState.value.copy(booking = updated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to update")
            }
        }
    }
}
