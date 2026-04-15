package com.drone.delivery.dto.response

import java.math.BigDecimal

data class CartItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productPrice: BigDecimal,
    val image: String?,
    val quantity: Int,
    val merchantId: Long
)
