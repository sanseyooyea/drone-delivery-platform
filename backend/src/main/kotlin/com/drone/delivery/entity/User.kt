package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("user")
data class User(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var username: String = "",
    var password: String = "",
    var phone: String? = null,
    var email: String? = null,
    var avatar: String? = null,
    var role: Int = 0,
    var status: Int = 1,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
