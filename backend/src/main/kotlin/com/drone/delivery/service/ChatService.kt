package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.drone.delivery.common.Constants
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.entity.ChatMessage
import com.drone.delivery.entity.ChatSession
import com.drone.delivery.mapper.ChatMessageMapper
import com.drone.delivery.mapper.ChatSessionMapper
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatSessionMapper: ChatSessionMapper,
    private val chatMessageMapper: ChatMessageMapper
) {
    fun createSession(userId: Long, orderId: Long?): ChatSession {
        val session = ChatSession(userId = userId, orderId = orderId)
        chatSessionMapper.insert(session)
        return session
    }

    fun listUserSessions(userId: Long): List<ChatSession> =
        chatSessionMapper.selectList(
            QueryWrapper<ChatSession>().eq("user_id", userId).orderByDesc("create_time")
        )

    fun listActiveSessions(): List<ChatSession> =
        chatSessionMapper.selectList(
            QueryWrapper<ChatSession>().eq("status", Constants.CHAT_ACTIVE).orderByAsc("create_time")
        )

    fun getMessages(sessionId: Long): List<ChatMessage> =
        chatMessageMapper.selectList(
            QueryWrapper<ChatMessage>().eq("session_id", sessionId).orderByAsc("send_time")
        )

    fun sendMessage(sessionId: Long, senderId: Long, senderRole: Int, content: String): ChatMessage {
        val session = chatSessionMapper.selectById(sessionId) ?: throw BizException(message = "会话不存在")
        if (session.status == Constants.CHAT_CLOSED) throw BizException(message = "会话已关闭")

        val message = ChatMessage(
            sessionId = sessionId,
            senderId = senderId,
            senderRole = senderRole,
            content = content
        )
        chatMessageMapper.insert(message)
        return message
    }

    fun closeSession(sessionId: Long) {
        val session = chatSessionMapper.selectById(sessionId) ?: throw BizException(message = "会话不存在")
        session.status = Constants.CHAT_CLOSED
        chatSessionMapper.updateById(session)
    }
}
