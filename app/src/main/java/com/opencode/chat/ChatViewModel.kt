package com.opencode.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.opencode.chat.data.ChatMessage
import com.opencode.chat.data.MessagePart
import com.opencode.chat.data.OpenCodeClient
import com.opencode.chat.data.SendMessageRequest
import kotlinx.coroutines.launch
import okhttp3.sse.EventSource
import java.util.UUID

class ChatViewModel : ViewModel() {

    val isConnected = mutableStateOf(false)
    val isConnecting = mutableStateOf(false)
    val connectionError = mutableStateOf<String?>(null)
    val sessionId = mutableStateOf<String?>(null)
    val messages = mutableStateListOf<ChatMessage>()
    val isSending = mutableStateOf(false)

    private var client: OpenCodeClient? = null
    private var eventSource: EventSource? = null

    fun connect(baseUrl: String, password: String) {
        isConnecting.value = true
        connectionError.value = null

        val newClient = OpenCodeClient(baseUrl, password.ifBlank { null })
        client = newClient

        viewModelScope.launch {
            try {
                val session = newClient.service.createSession()
                sessionId.value = session.id
                isConnected.value = true
            } catch (e: Exception) {
                connectionError.value = e.message ?: "فشل الاتصال بالخادم"
            } finally {
                isConnecting.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        val currentClient = client ?: return
        val currentSession = sessionId.value ?: return
        if (text.isBlank()) return

        messages.add(ChatMessage(UUID.randomUUID().toString(), "user", text))
        isSending.value = true

        viewModelScope.launch {
            try {
                // ملاحظة: هذا الاستدعاء يظل منتظرًا حتى يكمل الوكيل رده الكامل،
                // لأن /session/:id/message نقطة مزامنة (synchronous) وليست بثًا لحظيًا.
                val response = currentClient.service.sendMessage(
                    currentSession,
                    SendMessageRequest(parts = listOf(MessagePart(text = text)))
                )
                val replyText = response.parts
                    .asSequence()
                    .filter { it.type == "text" && !it.text.isNullOrBlank() }
                    .mapNotNull { it.text }
                    .joinToString("\n")

                if (replyText.isNotBlank()) {
                    val agentLabel = listOfNotNull(response.info?.providerID, response.info?.modelID)
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(" · ")
                    messages.add(
                        ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = "assistant",
                            text = replyText,
                            agentLabel = agentLabel
                        )
                    )
                } else {
                    connectionError.value = "وصل رد من الخادم لكن بدون نص واضح — راجع /doc لمطابقة شكل الأجزاء (parts)"
                }
            } catch (e: Exception) {
                connectionError.value = "فشل إرسال الرسالة: ${e.message}"
            } finally {
                isSending.value = false
            }
        }
    }

    override fun onCleared() {
        eventSource?.cancel()
        super.onCleared()
    }
}
