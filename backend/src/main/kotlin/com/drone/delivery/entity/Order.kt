package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.math.BigDecimal
import java.time.LocalDateTime

@TableName("`order`")
data class Order(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var orderNo: String = "",
    var userId: Long = 0,
    var merchantId: Long = 0,
    var addressId: Long? = null,
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    var status: Int = 0,
    var droneId: Long? = null,
    var remark: String? = null,
    var rejectReason: String? = null,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
