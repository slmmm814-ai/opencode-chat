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
                startListening(newClient)
            } catch (e: Exception) {
                connectionError.value = e.message ?: "فشل الاتصال بالخادم"
            } finally {
                isConnecting.value = false
            }
        }
    }

    private fun startListening(client: OpenCodeClient) {
        eventSource = client.listenEvents(
            onEvent = { data ->
                try {
                    val json = JsonParser.parseString(data).asJsonObject
                    val text = when {
                        json.has("text") -> json.get("text").asString
                        json.has("content") -> json.get("content").asString
                        else -> null
                    }
                    if (text != null) {
                        val agent = json.get("agent")?.asString
                            ?: json.get("mode")?.asString
                        val model = json.get("modelID")?.asString
                            ?: json.get("model")?.asString
                        val agentLabel = listOfNotNull(agent, model)
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString(" · ")
                        val thoughtMs = json.get("thinkingMs")?.asLong
                            ?: json.get("reasoningMs")?.asLong
                        val durationMs = json.get("durationMs")?.asLong
                            ?: json.get("elapsedMs")?.asLong

                        messages.add(
                            ChatMessage(
                                id = UUID.randomUUID().toString(),
                                role = "assistant",
                                text = text,
                                agentLabel = agentLabel,
                                thoughtMs = thoughtMs,
                                durationMs = durationMs
                            )
                        )
                    }
                } catch (_: Exception) {
                    // حدث غير متوقع الشكل — راجع مخطط /event الفعلي في /doc وعدّل هنا
                }
            },
            onError = { t ->
                connectionError.value = "انقطع الاتصال بالخادم: ${t?.message}"
            }
        )
    }

    fun sendMessage(text: String) {
        val currentClient = client ?: return
        val currentSession = sessionId.value ?: return
        if (text.isBlank()) return

        messages.add(ChatMessage(UUID.randomUUID().toString(), "user", text))
        isSending.value = true

        viewModelScope.launch {
            try {
                currentClient.service.sendMessage(
                    currentSession,
                    SendMessageRequest(parts = listOf(MessagePart(text = text)))
                )
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
