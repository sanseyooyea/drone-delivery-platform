package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("drone_log")
data class DroneLog(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var droneId: Long = 0,
    var orderId: Long = 0,
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var status: Int = 0
)
