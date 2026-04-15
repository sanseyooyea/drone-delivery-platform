package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.math.BigDecimal

@TableName("order_item")
data class OrderItem(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var orderId: Long = 0,
    var productId: Long = 0,
    var productName: String = "",
    var productPrice: BigDecimal = BigDecimal.ZERO,
    var quantity: Int = 0
)
