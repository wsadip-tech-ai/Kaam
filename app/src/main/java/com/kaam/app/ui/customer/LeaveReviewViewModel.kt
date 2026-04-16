package com.kaam.app.ui.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Review
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.BookingRepository
import com.kaam.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaveReviewUiState(
    val rating: Int = 0,
    val comment: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LeaveReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val bookingId: String = savedStateHandle["bookingId"] ?: ""

    private val _uiState = MutableStateFlow(LeaveReviewUiState())
    val uiState: StateFlow<LeaveReviewUiState> = _uiState

    fun setRating(rating: Int) {
        _uiState.value = _uiState.value.copy(rating = rating)
    }

    fun setComment(comment: String) {
        _uiState.value = _uiState.value.copy(comment = comment)
    }

    fun submit() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userId = authRepository.currentUserId!!
                val booking = bookingRepository.getBooking(bookingId)
                    ?: throw Exception("Booking not found")
                val review = Review(
                    bookingId = bookingId,
                    reviewerId = userId,
                    workerId = booking.workerId!!,
                    rating = _uiState.value.rating,
                    comment = _uiState.value.comment.ifBlank { null },
                )
                reviewRepository.createReview(review)
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to submit review",
                )
            }
        }
    }
}
