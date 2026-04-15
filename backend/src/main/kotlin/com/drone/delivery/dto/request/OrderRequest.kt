package com.drone.delivery.dto.request

import jakarta.validation.constraints.NotNull

data class CreateOrderRequest(
    @field:NotNull(message = "地址不能为空")
    val addressId: Long,
    val merchantId: Long,
    val items: List<OrderItemRequest>,
    val remark: String? = null
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)

data class RejectOrderRequest(
    val reason: String
)
