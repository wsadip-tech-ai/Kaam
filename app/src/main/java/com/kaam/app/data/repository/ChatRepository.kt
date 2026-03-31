package com.kaam.app.data.repository

import com.kaam.app.data.model.Conversation
import com.kaam.app.data.model.Message
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val realtime: Realtime,
) {

    // ---------- Conversation ----------

    suspend fun getConversation(conversationId: String): Conversation? {
        return postgrest.from("conversations")
            .select {
                filter { eq("id", conversationId) }
            }
            .decodeSingleOrNull<Conversation>()
    }

    suspend fun getUserConversations(userId: String): List<Conversation> {
        return postgrest.from("conversations")
            .select {
                filter {
                    or {
                        eq("customer_id", userId)
                        eq("worker_id", userId)
                    }
                }
            }
            .decodeList<Conversation>()
    }

    suspend fun getOrCreateConversation(conversation: Conversation): Conversation {
        // Try to find an existing conversation for the same booking or job request
        val existing = if (conversation.bookingId != null) {
            postgrest.from("conversations")
                .select {
                    filter { eq("booking_id", conversation.bookingId) }
                }
                .decodeSingleOrNull<Conversation>()
        } else if (conversation.jobRequestId != null) {
            postgrest.from("conversations")
                .select {
                    filter {
                        eq("job_request_id", conversation.jobRequestId)
                        eq("worker_id", conversation.workerId)
                    }
                }
                .decodeSingleOrNull<Conversation>()
        } else {
            null
        }
        return existing ?: postgrest.from("conversations")
            .insert(conversation) {
                select()
            }
            .decodeSingle<Conversation>()
    }

    // ---------- Messages ----------

    suspend fun getMessages(conversationId: String): List<Message> {
        return postgrest.from("messages")
            .select {
                filter { eq("conversation_id", conversationId) }
            }
            .decodeList<Message>()
    }

    suspend fun sendMessage(message: Message): Message {
        return postgrest.from("messages")
            .insert(message) {
                select()
            }
            .decodeSingle<Message>()
    }

    suspend fun markMessageRead(messageId: String, readAt: String): Message {
        return postgrest.from("messages")
            .update({ set("read_at", readAt) }) {
                select()
                filter { eq("id", messageId) }
            }
            .decodeSingle<Message>()
    }

    // ---------- Realtime ----------

    /**
     * Returns a cold Flow that emits new [Message] objects inserted into the given conversation.
     * Caller must subscribe the channel and manage its lifecycle.
     */
    fun messageInsertFlow(conversationId: String): Flow<Message> {
        val channel = realtime.channel("messages:$conversationId")
        val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "messages"
            filter("conversation_id", FilterOperator.EQ, conversationId)
        }
        return changeFlow.map { action ->
            action.decodeRecord<Message>()
        }
    }

    /**
     * Subscribes a Realtime channel for a conversation. Returns the channel so the
     * caller can unsubscribe when the UI is no longer active.
     */
    suspend fun subscribeToConversation(conversationId: String): RealtimeChannel {
        val channel = realtime.channel("messages:$conversationId")
        channel.subscribe()
        return channel
    }
}
