package com.drone.delivery.dto.response

import java.math.BigDecimal

data class AdminStatsResponse(
    val totalUsers: Long,
    val totalMerchants: Long,
    val totalOrders: Long,
    val todayOrders: Long,
    val todaySales: BigDecimal
)

data class MerchantStatsResponse(
    val todayOrders: Long,
    val totalSales: BigDecimal
)
