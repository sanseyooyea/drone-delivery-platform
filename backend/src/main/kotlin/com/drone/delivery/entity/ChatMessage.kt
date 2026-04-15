package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("chat_message")
data class ChatMessage(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var sessionId: Long = 0,
    var senderId: Long = 0,
    var senderRole: Int = 0,
    var content: String = "",
    var sendTime: LocalDateTime? = null
)
