package com.opencode.chat.data

data class CreateSessionResponse(
    val id: String,
    val title: String? = null
)

data class MessagePart(
    val type: String = "text",
    val text: String
)

data class SendMessageRequest(
    val parts: List<MessagePart>,
    val providerID: String? = null,
    val modelID: String? = null
)

data class ChatMessage(
    val id: String,
    val role: String,
    val text: String
)
