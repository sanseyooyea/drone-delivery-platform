package com.drone.delivery.common

object Constants {
    // 用户角色
    const val ROLE_USER = 0
    const val ROLE_MERCHANT = 1
    const val ROLE_ADMIN = 2

    // 用户状态
    const val STATUS_DISABLED = 0
    const val STATUS_ENABLED = 1

    // 商家审核状态
    const val AUDIT_PENDING = 0
    const val AUDIT_APPROVED = 1
    const val AUDIT_REJECTED = 2

    // 订单状态
    const val ORDER_PENDING = 0        // 待接单
    const val ORDER_ACCEPTED = 1       // 已接单/备货中
    const val ORDER_DELIVERING = 2     // 配送中
    const val ORDER_COMPLETED = 3      // 已送达
    const val ORDER_CANCELLED = 4      // 已取消
    const val ORDER_REJECTED = 5       // 已拒单

    // 无人机状态
    const val DRONE_IDLE = 0           // 空闲
    const val DRONE_DELIVERING = 1     // 配送中
    const val DRONE_MAINTENANCE = 2    // 维护中

    // 客服会话状态
    const val CHAT_ACTIVE = 0
    const val CHAT_CLOSED = 1
}
