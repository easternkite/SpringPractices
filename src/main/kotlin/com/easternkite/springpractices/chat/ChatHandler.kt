package com.easternkite.springpractices.chat

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler(
    private val redisTemplate: RedisTemplate<String, Any>
) : TextWebSocketHandler() {
    private val sessions = mutableSetOf<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        sessions += session
        println("연결됨 : ${session.id}")

        val chatSession = ChatSession(
            sessionId = session.id,
            userId = extractUserId(session),
            connectedAt = System.currentTimeMillis()
        )

        redisTemplate.opsForValue().set("chat:session:${chatSession.sessionId}", chatSession)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        super.handleTextMessage(session, message)

        for (s in sessions) {
            if (s.isOpen) {
                val chat = redisTemplate.opsForValue().get("chat:session:${session.id}") as ChatSession
                s.sendMessage(TextMessage("${chat.userId}: ${message.payload}"))
            } else {
                sessions.remove(s)
                redisTemplate.delete("chat:session:${s.id}")
                println("세션 제거됨 : ${s.id}")
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        super.afterConnectionClosed(session, status)
        sessions -= session
        redisTemplate.delete("chat:session:${session.id}")
        println("연결 종료됨 : ${session.id}")
    }

    private fun extractUserId(session: WebSocketSession): String? {
        val uri = session.uri ?: return null
        return uri.query?.split("&")?.firstOrNull { it.startsWith("userId=") }
            ?.substringAfter("userId=")
    }
}