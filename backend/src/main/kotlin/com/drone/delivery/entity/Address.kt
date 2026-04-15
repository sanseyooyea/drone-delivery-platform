package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("address")
data class Address(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long = 0,
    var contactName: String = "",
    var contactPhone: String = "",
    var address: String = "",
    var isDefault: Int = 0,
    var createTime: LocalDateTime? = null
)
