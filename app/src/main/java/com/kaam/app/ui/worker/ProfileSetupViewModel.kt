package com.kaam.app.ui.worker

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.ServiceType
import com.kaam.app.data.model.WorkerProfile
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ProfileRepository
import com.kaam.app.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSetupUiState(
    val services: Set<ServiceType> = emptySet(),
    val hourlyRate: Int = 500,
    val bio: String = "",
    val citizenshipDocUri: Uri? = null,
    val paymentQrUri: Uri? = null,
    val paymentPhone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false,
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState

    fun toggleService(service: ServiceType) {
        val current = _uiState.value.services.toMutableSet()
        if (current.contains(service)) current.remove(service) else current.add(service)
        _uiState.value = _uiState.value.copy(services = current)
    }

    fun setHourlyRate(rate: Int) {
        _uiState.value = _uiState.value.copy(hourlyRate = rate)
    }

    fun setBio(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    fun setCitizenshipDoc(uri: Uri) {
        _uiState.value = _uiState.value.copy(citizenshipDocUri = uri)
    }

    fun setPaymentQr(uri: Uri) {
        _uiState.value = _uiState.value.copy(paymentQrUri = uri)
    }

    fun setPaymentPhone(phone: String) {
        _uiState.value = _uiState.value.copy(paymentPhone = phone)
    }

    fun submit(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userId = authRepository.currentUserId!!
                val state = _uiState.value

                // Upload citizenship doc
                var docUrl: String? = null
                state.citizenshipDocUri?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                        ?: throw Exception("Cannot read document")
                    docUrl = storageRepository.uploadDocument(userId, bytes, "citizenship.jpg")
                }

                // Upload payment QR (optional)
                var qrUrl: String? = null
                state.paymentQrUri?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                        ?: throw Exception("Cannot read QR image")
                    qrUrl = storageRepository.uploadPaymentQr(userId, bytes)
                }

                val workerProfile = WorkerProfile(
                    id = userId,
                    bio = state.bio.ifBlank { null },
                    services = state.services.toList(),
                    hourlyRate = state.hourlyRate,
                    citizenshipDocUrl = docUrl,
                    paymentQrUrl = qrUrl,
                    paymentPhone = state.paymentPhone.ifBlank { null },
                )
                profileRepository.createWorkerProfile(workerProfile)
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create profile",
                )
            }
        }
    }
}
