package com.drone.delivery.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "用户名不能为空")
    val username: String,
    @field:NotBlank(message = "密码不能为空")
    val password: String
)

data class RegisterRequest(
    @field:NotBlank(message = "用户名不能为空")
    val username: String,
    @field:NotBlank(message = "密码不能为空")
    val password: String,
    val phone: String? = null,
    val email: String? = null
)

data class UpdateProfileRequest(
    val phone: String? = null,
    val email: String? = null,
    val avatar: String? = null
)

data class UpdatePasswordRequest(
    @field:NotBlank(message = "旧密码不能为空")
    val oldPassword: String,
    @field:NotBlank(message = "新密码不能为空")
    val newPassword: String
)
