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

data class ResponsePart(
    val type: String? = null,
    val text: String? = null
)

data class MessageInfo(
    val id: String? = null,
    val role: String? = null,
    val modelID: String? = null,
    val providerID: String? = null
)

data class MessageResponse(
    val info: MessageInfo? = null,
    val parts: List<ResponsePart> = emptyList()
)

data class ChatMessage(
    val id: String,
    val role: String,
    val text: String,
    val agentLabel: String? = null,
    val thoughtMs: Long? = null,
    val durationMs: Long? = null
)
