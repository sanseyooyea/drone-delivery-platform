package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("merchant")
data class Merchant(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long = 0,
    var shopName: String = "",
    var contact: String? = null,
    var category: String? = null,
    var description: String? = null,
    var auditStatus: Int = 0,
    var auditRemark: String? = null,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
