package com.kaam.app.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Message
import com.kaam.app.data.model.Profile
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ChatRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatRoomUiState(
    val messages: List<Message> = emptyList(),
    val otherParty: Profile? = null,
    val currentUserId: String = "",
    val messageText: String = "",
    val isLoading: Boolean = true,
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val conversationId: String = savedStateHandle["conversationId"] ?: ""

    private val _uiState = MutableStateFlow(ChatRoomUiState())
    val uiState: StateFlow<ChatRoomUiState> = _uiState

    private var realtimeChannel: RealtimeChannel? = null

    init {
        loadChat()
        subscribeToMessages()
    }

    private fun loadChat() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val conversation = chatRepository.getConversation(conversationId) ?: return@launch
            val otherId = if (conversation.customerId == userId) conversation.workerId else conversation.customerId
            val otherProfile = profileRepository.getProfile(otherId)
            val messages = chatRepository.getMessages(conversationId)
                .sortedBy { it.sentAt }
            _uiState.value = ChatRoomUiState(
                messages = messages,
                otherParty = otherProfile,
                currentUserId = userId,
                isLoading = false,
            )
        }
    }

    private fun subscribeToMessages() {
        viewModelScope.launch {
            realtimeChannel = chatRepository.subscribeToConversation(conversationId)
            chatRepository.messageInsertFlow(conversationId)
                .catch { /* ignore realtime errors */ }
                .collect { newMessage ->
                    val current = _uiState.value.messages
                    if (current.none { it.id == newMessage.id }) {
                        _uiState.value = _uiState.value.copy(messages = current + newMessage)
                    }
                }
        }
    }

    fun setMessageText(text: String) {
        _uiState.value = _uiState.value.copy(messageText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.messageText.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val message = Message(
                conversationId = conversationId,
                senderId = userId,
                content = text,
            )
            chatRepository.sendMessage(message)
            _uiState.value = _uiState.value.copy(messageText = "")
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            realtimeChannel?.unsubscribe()
        }
    }
}
