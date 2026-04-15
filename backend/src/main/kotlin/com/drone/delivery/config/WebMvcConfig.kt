package com.drone.delivery.config

import com.drone.delivery.common.exception.BizException
import com.drone.delivery.util.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(private val jwtUtil: JwtUtil) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(JwtInterceptor(jwtUtil))
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/login",
                "/api/auth/register",
                "/api/products",
                "/api/products/**",
                "/api/categories"
            )
    }

    class JwtInterceptor(private val jwtUtil: JwtUtil) : HandlerInterceptor {
        override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
            if (request.method == "OPTIONS") return true

            val token = request.getHeader("Authorization")?.removePrefix("Bearer ")?.trim()
            if (token.isNullOrBlank()) {
                throw BizException(401, "未登录")
            }

            return try {
                val claims = jwtUtil.parseToken(token)
                request.setAttribute("userId", claims.subject.toLong())
                request.setAttribute("role", claims["role"] as Int)
                true
            } catch (e: Exception) {
                throw BizException(401, "Token 无效或已过期")
            }
        }
    }
}
