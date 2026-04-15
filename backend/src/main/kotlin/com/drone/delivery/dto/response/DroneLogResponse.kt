package com.drone.delivery.dto.response

import java.time.LocalDateTime

data class DroneLogResponse(
    val id: Long,
    val droneId: Long,
    val orderId: Long,
    val orderNo: String,
    val deliveryAddress: String?,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val status: Int
)
