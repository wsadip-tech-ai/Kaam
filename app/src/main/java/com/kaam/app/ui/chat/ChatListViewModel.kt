package com.kaam.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaam.app.data.model.Conversation
import com.kaam.app.data.model.Message
import com.kaam.app.data.model.Profile
import com.kaam.app.data.repository.AuthRepository
import com.kaam.app.data.repository.ChatRepository
import com.kaam.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationPreview(
    val conversation: Conversation,
    val otherParty: Profile,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
)

data class ChatListUiState(
    val conversations: List<ConversationPreview> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            val conversations = chatRepository.getUserConversations(userId)
            val previews = conversations.mapNotNull { convo ->
                val otherId = if (convo.customerId == userId) convo.workerId else convo.customerId
                val otherProfile = profileRepository.getProfile(otherId) ?: return@mapNotNull null
                val messages = chatRepository.getMessages(convo.id!!)
                val lastMessage = messages.maxByOrNull { it.sentAt ?: "" }
                val unread = messages.count { it.senderId != userId && it.readAt == null }
                ConversationPreview(
                    conversation = convo,
                    otherParty = otherProfile,
                    lastMessage = lastMessage,
                    unreadCount = unread,
                )
            }
            _uiState.value = ChatListUiState(conversations = previews, isLoading = false)
        }
    }
}
