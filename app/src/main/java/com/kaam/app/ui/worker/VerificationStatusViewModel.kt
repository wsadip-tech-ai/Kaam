package com.kaam.app.ui.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.WorkerProfile
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationStatusViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _workerProfile = MutableStateFlow<WorkerProfile?>(null)
    val workerProfile: StateFlow<WorkerProfile?> = _workerProfile

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            _workerProfile.value = profileRepository.getWorkerProfile(userId)
        }
    }
}
