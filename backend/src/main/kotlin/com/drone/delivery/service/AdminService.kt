package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.drone.delivery.common.Constants
import com.drone.delivery.common.PageResult
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.dto.response.AdminStatsResponse
import com.drone.delivery.entity.Merchant
import com.drone.delivery.entity.Order
import com.drone.delivery.entity.User
import com.drone.delivery.mapper.MerchantMapper
import com.drone.delivery.mapper.OrderMapper
import com.drone.delivery.mapper.UserMapper
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class AdminService(
    private val userMapper: UserMapper,
    private val merchantMapper: MerchantMapper,
    private val orderMapper: OrderMapper
) {
    fun listUsers(page: Long, size: Long): PageResult<User> {
        val wrapper = QueryWrapper<User>()
            .ne("role", Constants.ROLE_ADMIN)
            .orderByDesc("create_time")
        val result = userMapper.selectPage(Page(page, size), wrapper)
        result.records.forEach { it.password = "" }
        return PageResult(result.total, result.records)
    }

    fun toggleUserStatus(id: Long, status: Int) {
        val user = userMapper.selectById(id) ?: throw BizException(message = "用户不存在")
        user.status = status
        userMapper.updateById(user)
    }

    fun getStats(): AdminStatsResponse {
        val totalUsers = userMapper.selectCount(
            QueryWrapper<User>().eq("role", Constants.ROLE_USER)
        )
        val totalMerchants = merchantMapper.selectCount(
            QueryWrapper<Merchant>().eq("audit_status", Constants.AUDIT_APPROVED)
        )
        val totalOrders = orderMapper.selectCount(QueryWrapper<Order>())

        val todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        val todayOrders = orderMapper.selectCount(
            QueryWrapper<Order>().ge("create_time", todayStart)
        )

        val todayCompletedOrders = orderMapper.selectList(
            QueryWrapper<Order>()
                .ge("create_time", todayStart)
                .eq("status", Constants.ORDER_COMPLETED)
        )
        val todaySales = todayCompletedOrders.sumOf { it.totalPrice }

        return AdminStatsResponse(totalUsers, totalMerchants, totalOrders, todayOrders, todaySales)
    }
}
