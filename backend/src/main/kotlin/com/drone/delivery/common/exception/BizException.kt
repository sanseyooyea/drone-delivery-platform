package com.drone.delivery.common.exception

class BizException(
    val code: Int = 400,
    override val message: String
) : RuntimeException(message)
