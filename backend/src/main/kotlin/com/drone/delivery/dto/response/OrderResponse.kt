package com.drone.delivery.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDetailResponse(
    val id: Long,
    val orderNo: String,
    val merchantName: String,
    val totalPrice: BigDecimal,
    val status: Int,
    val droneCode: String?,
    val remark: String?,
    val rejectReason: String?,
    val items: List<OrderItemResponse>,
    val address: AddressResponse?,
    val createTime: LocalDateTime?
)

data class OrderItemResponse(
    val productId: Long,
    val productName: String,
    val productPrice: BigDecimal,
    val quantity: Int
)

data class AddressResponse(
    val contactName: String,
    val contactPhone: String,
    val address: String
)
