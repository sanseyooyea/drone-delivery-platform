package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.dto.request.LoginRequest
import com.drone.delivery.dto.request.RegisterRequest
import com.drone.delivery.dto.request.UpdatePasswordRequest
import com.drone.delivery.dto.request.UpdateProfileRequest
import com.drone.delivery.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterRequest): Result<Nothing> {
        authService.register(req)
        return Result.ok("注册成功")
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest) =
        Result.ok(data = authService.login(req))

    @GetMapping("/profile")
    fun getProfile(request: HttpServletRequest) =
        Result.ok(data = authService.getProfile(request.getAttribute("userId") as Long))

    @PutMapping("/profile")
    fun updateProfile(request: HttpServletRequest, @RequestBody req: UpdateProfileRequest): Result<Nothing> {
        authService.updateProfile(request.getAttribute("userId") as Long, req)
        return Result.ok("修改成功")
    }

    @PutMapping("/password")
    fun updatePassword(request: HttpServletRequest, @Valid @RequestBody req: UpdatePasswordRequest): Result<Nothing> {
        authService.updatePassword(request.getAttribute("userId") as Long, req)
        return Result.ok("密码修改成功")
    }
}
