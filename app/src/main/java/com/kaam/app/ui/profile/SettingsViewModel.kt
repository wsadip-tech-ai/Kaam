package com.kaam.app.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Profile
import com.kaam.app.data.model.UserRole
import com.kaam.app.data.model.WorkerProfile
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ProfileRepository
import com.kaam.app.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val profile: Profile? = null,
    val workerProfile: WorkerProfile? = null,
    val fullName: String = "",
    val city: String = "",
    val area: String = "",
    val bio: String = "",
    val hourlyRate: Int = 500,
    val isAvailable: Boolean = true,
    val avatarUri: Uri? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isLoggedOut: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val profile = profileRepository.getProfile(userId)
            val wp = if (profile?.role == UserRole.WORKER) {
                profileRepository.getWorkerProfile(userId)
            } else null

            _uiState.value = SettingsUiState(
                profile = profile,
                workerProfile = wp,
                fullName = profile?.fullName ?: "",
                city = profile?.city ?: "",
                area = profile?.area ?: "",
                bio = wp?.bio ?: "",
                hourlyRate = wp?.hourlyRate ?: 500,
                isAvailable = wp?.isAvailable ?: true,
                isLoading = false,
            )
        }
    }

    fun setFullName(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name)
    }

    fun setCity(city: String) {
        _uiState.value = _uiState.value.copy(city = city)
    }

    fun setArea(area: String) {
        _uiState.value = _uiState.value.copy(area = area)
    }

    fun setBio(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    fun setHourlyRate(rate: Int) {
        _uiState.value = _uiState.value.copy(hourlyRate = rate)
    }

    fun setAvatarUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(avatarUri = uri)
    }

    fun toggleAvailability() {
        _uiState.value = _uiState.value.copy(isAvailable = !_uiState.value.isAvailable)
    }

    fun save(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val userId = authRepository.currentUserId!!
                val state = _uiState.value
                val profile = state.profile ?: return@launch

                // Upload avatar if changed
                var avatarUrl = profile.avatarUrl
                state.avatarUri?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                        ?: throw Exception("Cannot read image")
                    avatarUrl = storageRepository.uploadAvatar(userId, bytes)
                }

                val updatedProfile = profile.copy(
                    fullName = state.fullName,
                    city = state.city,
                    area = state.area.ifBlank { null },
                    avatarUrl = avatarUrl,
                )
                profileRepository.updateProfile(updatedProfile)

                // Update worker profile if worker
                if (state.workerProfile != null) {
                    val updatedWp = state.workerProfile.copy(
                        bio = state.bio.ifBlank { null },
                        hourlyRate = state.hourlyRate,
                        isAvailable = state.isAvailable,
                    )
                    profileRepository.updateWorkerProfile(updatedWp)
                }

                _uiState.value = _uiState.value.copy(isSaving = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save",
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }
    }
}
