package com.kaam.app.ui.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Review
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyReviewsUiState(
    val reviews: List<Review> = emptyList(),
    val averageRating: Double? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class MyReviewsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsUiState())
    val uiState: StateFlow<MyReviewsUiState> = _uiState

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val reviews = reviewRepository.getWorkerReviews(userId)
            val avg = if (reviews.isEmpty()) null else reviews.map { it.rating }.average()
            _uiState.value = MyReviewsUiState(
                reviews = reviews,
                averageRating = avg,
                isLoading = false,
            )
        }
    }
}
