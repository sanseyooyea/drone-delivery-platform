package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.dto.request.LoginRequest
import com.drone.delivery.dto.request.RegisterRequest
import com.drone.delivery.dto.request.UpdatePasswordRequest
import com.drone.delivery.dto.request.UpdateProfileRequest
import com.drone.delivery.dto.response.LoginResponse
import com.drone.delivery.dto.response.UserInfoResponse
import com.drone.delivery.entity.User
import com.drone.delivery.mapper.UserMapper
import com.drone.delivery.util.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userMapper: UserMapper,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun register(req: RegisterRequest) {
        val existing = userMapper.selectOne(
            QueryWrapper<User>().eq("username", req.username)
        )
        if (existing != null) throw BizException(message = "用户名已存在")

        val user = User(
            username = req.username,
            password = passwordEncoder.encode(req.password),
            phone = req.phone,
            email = req.email
        )
        userMapper.insert(user)
    }

    fun login(req: LoginRequest): LoginResponse {
        val user = userMapper.selectOne(
            QueryWrapper<User>().eq("username", req.username)
        ) ?: throw BizException(message = "用户名或密码错误")

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw BizException(message = "用户名或密码错误")
        }
        if (user.status == 0) throw BizException(message = "账号已被禁用")

        val token = jwtUtil.generateToken(user.id!!, user.role)
        return LoginResponse(token, user.id!!, user.username, user.role)
    }

    fun getProfile(userId: Long): UserInfoResponse {
        val user = userMapper.selectById(userId) ?: throw BizException(message = "用户不存在")
        return UserInfoResponse(user.id!!, user.username, user.phone, user.email, user.avatar, user.role)
    }

    fun updateProfile(userId: Long, req: UpdateProfileRequest) {
        val user = userMapper.selectById(userId) ?: throw BizException(message = "用户不存在")
        req.phone?.let { user.phone = it }
        req.email?.let { user.email = it }
        req.avatar?.let { user.avatar = it }
        userMapper.updateById(user)
    }

    fun updatePassword(userId: Long, req: UpdatePasswordRequest) {
        val user = userMapper.selectById(userId) ?: throw BizException(message = "用户不存在")
        if (!passwordEncoder.matches(req.oldPassword, user.password)) {
            throw BizException(message = "旧密码错误")
        }
        user.password = passwordEncoder.encode(req.newPassword)
        userMapper.updateById(user)
    }
}
