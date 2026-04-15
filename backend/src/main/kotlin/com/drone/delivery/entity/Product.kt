package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.math.BigDecimal
import java.time.LocalDateTime

@TableName("product")
data class Product(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var merchantId: Long = 0,
    var categoryId: Long? = null,
    var name: String = "",
    var price: BigDecimal = BigDecimal.ZERO,
    var image: String? = null,
    var description: String? = null,
    var status: Int = 1,
    var sales: Int = 0,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
