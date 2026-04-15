package com.drone.delivery.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("category")
data class Category(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var name: String = "",
    var sortOrder: Int = 0,
    var createTime: LocalDateTime? = null
)
