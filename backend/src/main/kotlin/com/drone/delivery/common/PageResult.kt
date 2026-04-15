package com.drone.delivery.common

data class PageResult<T>(
    val total: Long,
    val list: List<T>
)
