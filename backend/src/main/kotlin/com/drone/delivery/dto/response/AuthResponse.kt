package com.drone.delivery.dto.response

data class LoginResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val role: Int
)

data class UserInfoResponse(
    val id: Long,
    val username: String,
    val phone: String?,
    val email: String?,
    val avatar: String?,
    val role: Int
)
