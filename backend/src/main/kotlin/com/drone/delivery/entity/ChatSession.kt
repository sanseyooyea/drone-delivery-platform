package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("chat_session")
data class ChatSession(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long = 0,
    var adminId: Long? = null,
    var orderId: Long? = null,
    var status: Int = 0,
    var createTime: LocalDateTime? = null
)
