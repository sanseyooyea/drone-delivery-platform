package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("drone")
data class Drone(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var code: String = "",
    var model: String? = null,
    var status: Int = 0,
    var createTime: LocalDateTime? = null
)
