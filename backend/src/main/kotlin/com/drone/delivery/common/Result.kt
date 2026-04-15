package com.drone.delivery.common

data class Result<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> ok(data: T? = null): Result<T> =
            Result(200, "success", data)

        fun <T> ok(message: String, data: T? = null): Result<T> =
            Result(200, message, data)

        fun <T> error(code: Int = 500, message: String): Result<T> =
            Result(code, message, null)
    }
}
