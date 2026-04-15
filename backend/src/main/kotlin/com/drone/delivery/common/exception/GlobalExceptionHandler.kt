package com.drone.delivery.common.exception

import com.drone.delivery.common.Result
import org.slf4j.LoggerFactory
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BizException::class)
    fun handleBiz(e: BizException): Result<Nothing> =
        Result.error(e.code, e.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): Result<Nothing> {
        val msg = e.bindingResult.allErrors.firstOrNull()?.defaultMessage ?: "参数校验失败"
        return Result.error(400, msg)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Result<Nothing> {
        log.error("系统异常", e)
        return Result.error(500, "服务器内部错误")
    }
}
