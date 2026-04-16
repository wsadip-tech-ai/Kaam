package com.kaam.app.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Booking
import com.kaam.app.data.model.BookingType
import com.kaam.app.data.model.ServiceType
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingFormUiState(
    val service: ServiceType = ServiceType.CLEANING,
    val date: String = "",
    val timeSlot: String = "",
    val address: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false,
)

@HiltViewModel
class BookingFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
) : ViewModel() {

    private val workerId: String = savedStateHandle["workerId"] ?: ""
    private val serviceArg: String = savedStateHandle["service"] ?: "cleaning"

    private val _uiState = MutableStateFlow(
        BookingFormUiState(
            service = ServiceType.entries.find { it.name.equals(serviceArg, ignoreCase = true) }
                ?: ServiceType.CLEANING,
        )
    )
    val uiState: StateFlow<BookingFormUiState> = _uiState

    fun setDate(date: String) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun setTimeSlot(timeSlot: String) {
        _uiState.value = _uiState.value.copy(timeSlot = timeSlot)
    }

    fun setAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun setNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun submit() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userId = authRepository.currentUserId!!
                val state = _uiState.value
                val booking = Booking(
                    customerId = userId,
                    workerId = workerId,
                    type = BookingType.DIRECT,
                    service = state.service,
                    date = state.date,
                    timeSlot = state.timeSlot,
                    address = state.address,
                    notes = state.notes.ifBlank { null },
                )
                bookingRepository.createBooking(booking)
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create booking",
                )
            }
        }
    }
}
