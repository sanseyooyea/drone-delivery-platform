package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.service.ChatService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*

data class ChatMessageRequest(
    val sessionId: Long,
    val content: String,
    val senderId: Long,
    val senderRole: Int
)

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService,
    private val messagingTemplate: SimpMessagingTemplate
) {
    @PostMapping("/sessions")
    fun createSession(
        request: HttpServletRequest,
        @RequestParam(required = false) orderId: Long?
    ) = Result.ok(data = chatService.createSession(request.getAttribute("userId") as Long, orderId))

    @GetMapping("/sessions")
    fun listSessions(request: HttpServletRequest) =
        Result.ok(data = chatService.listUserSessions(request.getAttribute("userId") as Long))

    @GetMapping("/sessions/{id}/messages")
    fun getMessages(@PathVariable id: Long) =
        Result.ok(data = chatService.getMessages(id))

    // WebSocket 消息处理
    @MessageMapping("/chat.send")
    fun handleChatMessage(@Payload msg: ChatMessageRequest) {
        val message = chatService.sendMessage(msg.sessionId, msg.senderId, msg.senderRole, msg.content)
        messagingTemplate.convertAndSend("/topic/chat/${msg.sessionId}", message)
    }
}
