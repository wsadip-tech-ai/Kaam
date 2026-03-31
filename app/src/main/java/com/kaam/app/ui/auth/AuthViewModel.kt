package com.kaam.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Profile
import com.kaam.app.data.model.UserRole
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val otpSent: Boolean = false,
    val isAuthenticated: Boolean = false,
    val hasProfile: Boolean = false,
    val userRole: UserRole? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ---------- OTP ----------

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { authRepository.sendOtp(phone) }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, otpSent = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun verifyOtp(phone: String, token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { authRepository.verifyOtp(phone, token) }
                .onSuccess {
                    val userId = authRepository.currentUserId
                    if (userId != null) {
                        checkProfile(userId)
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, isAuthenticated = true)
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private suspend fun checkProfile(userId: String) {
        runCatching { profileRepository.getProfile(userId) }
            .onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        hasProfile = profile != null,
                        userRole = profile?.role,
                    )
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        error = e.message,
                    )
                }
            }
    }

    // ---------- Role selection ----------

    fun selectRole(role: UserRole) {
        _uiState.update { it.copy(userRole = role) }
    }

    // ---------- Profile creation ----------

    fun createProfile(profile: Profile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { profileRepository.createProfile(profile) }
                .onSuccess { created ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasProfile = true,
                            userRole = created.role,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    // ---------- Sign out ----------

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { authRepository.signOut() }
                .onSuccess {
                    _uiState.update { AuthUiState() }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    // ---------- Error ----------

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
