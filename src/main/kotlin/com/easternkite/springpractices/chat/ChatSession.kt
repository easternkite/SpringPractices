package com.easternkite.springpractices.chat

data class ChatSession(
    val sessionId: String = "",
    val userId: String? = null,
    val connectedAt: Long = System.currentTimeMillis(),
)