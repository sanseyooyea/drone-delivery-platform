package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.drone.delivery.common.Constants
import com.drone.delivery.common.PageResult
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.entity.*
import com.drone.delivery.mapper.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class OrderService(
    private val orderMapper: OrderMapper,
    private val orderItemMapper: OrderItemMapper,
    private val productMapper: ProductMapper,
    private val droneMapper: DroneMapper,
    private val droneLogMapper: DroneLogMapper,
    private val cartService: CartService
) {
    @Transactional
    fun createOrder(userId: Long, merchantId: Long, addressId: Long, items: List<Pair<Long, Int>>, remark: String?): Order {
        val order = Order(
            orderNo = generateOrderNo(),
            userId = userId,
            merchantId = merchantId,
            addressId = addressId,
            remark = remark,
            status = Constants.ORDER_PENDING
        )

        var totalPrice = BigDecimal.ZERO
        val orderItems = items.map { (productId, quantity) ->
            val product = productMapper.selectById(productId) ?: throw BizException(message = "商品不存在: $productId")
            totalPrice = totalPrice.add(product.price.multiply(BigDecimal(quantity)))
            OrderItem(
                productId = productId,
                productName = product.name,
                productPrice = product.price,
                quantity = quantity
            )
        }

        order.totalPrice = totalPrice
        orderMapper.insert(order)

        orderItems.forEach {
            it.orderId = order.id!!
            orderItemMapper.insert(it)
        }

        cartService.clearCartItems(userId, items.map { it.first })

        return order
    }

    fun listUserOrders(userId: Long, status: Int?, page: Long, size: Long): PageResult<Order> {
        val wrapper = QueryWrapper<Order>()
            .eq("user_id", userId)
            .eq(status != null, "status", status)
            .orderByDesc("create_time")
        val result = orderMapper.selectPage(Page(page, size), wrapper)
        return PageResult(result.total, result.records)
    }

    fun getOrder(id: Long): Order =
        orderMapper.selectById(id) ?: throw BizException(message = "订单不存在")

    fun getOrderItems(orderId: Long): List<OrderItem> =
        orderItemMapper.selectList(QueryWrapper<OrderItem>().eq("order_id", orderId))

    fun cancelOrder(userId: Long, id: Long) {
        val order = getOrder(id)
        if (order.userId != userId) throw BizException(message = "无权操作")
        if (order.status != Constants.ORDER_PENDING) throw BizException(message = "当前状态不可取消")
        order.status = Constants.ORDER_CANCELLED
        orderMapper.updateById(order)
    }

    fun confirmReceive(userId: Long, id: Long) {
        val order = getOrder(id)
        if (order.userId != userId) throw BizException(message = "无权操作")
        if (order.status != Constants.ORDER_DELIVERING) throw BizException(message = "当前状态不可确认收货")
        order.status = Constants.ORDER_COMPLETED
        orderMapper.updateById(order)

        // 释放无人机
        order.droneId?.let { droneId ->
            val drone = droneMapper.selectById(droneId)
            drone?.let {
                it.status = Constants.DRONE_IDLE
                droneMapper.updateById(it)
            }
            val log = droneLogMapper.selectOne(
                QueryWrapper<DroneLog>()
                    .eq("order_id", id)
                    .eq("status", 0)
            )
            log?.let {
                it.status = 1
                it.endTime = LocalDateTime.now()
                droneLogMapper.updateById(it)
            }
        }
    }

    // 商家操作
    fun listMerchantOrders(merchantId: Long, status: Int?, page: Long, size: Long): PageResult<Order> {
        val wrapper = QueryWrapper<Order>()
            .eq("merchant_id", merchantId)
            .eq(status != null, "status", status)
            .orderByDesc("create_time")
        val result = orderMapper.selectPage(Page(page, size), wrapper)
        return PageResult(result.total, result.records)
    }

    fun acceptOrder(merchantId: Long, id: Long) {
        val order = getOrder(id)
        if (order.merchantId != merchantId) throw BizException(message = "无权操作")
        if (order.status != Constants.ORDER_PENDING) throw BizException(message = "当前状态不可接单")
        order.status = Constants.ORDER_ACCEPTED
        orderMapper.updateById(order)
    }

    fun rejectOrder(merchantId: Long, id: Long, reason: String) {
        val order = getOrder(id)
        if (order.merchantId != merchantId) throw BizException(message = "无权操作")
        if (order.status != Constants.ORDER_PENDING) throw BizException(message = "当前状态不可拒单")
        order.status = Constants.ORDER_REJECTED
        order.rejectReason = reason
        orderMapper.updateById(order)
    }

    @Transactional
    fun markReady(merchantId: Long, id: Long) {
        val order = getOrder(id)
        if (order.merchantId != merchantId) throw BizException(message = "无权操作")
        if (order.status != Constants.ORDER_ACCEPTED) throw BizException(message = "当前状态不可标记备货完成")

        // 分配空闲无人机
        val drone = droneMapper.selectOne(
            QueryWrapper<Drone>().eq("status", Constants.DRONE_IDLE).last("LIMIT 1")
        ) ?: throw BizException(message = "暂无空闲无人机，请稍后重试")

        drone.status = Constants.DRONE_DELIVERING
        droneMapper.updateById(drone)

        order.status = Constants.ORDER_DELIVERING
        order.droneId = drone.id
        orderMapper.updateById(order)

        droneLogMapper.insert(DroneLog(droneId = drone.id!!, orderId = order.id!!))
    }

    // 管理端
    fun listAllOrders(status: Int?, merchantId: Long?, page: Long, size: Long): PageResult<Order> {
        val wrapper = QueryWrapper<Order>()
            .eq(status != null, "status", status)
            .eq(merchantId != null, "merchant_id", merchantId)
            .orderByDesc("create_time")
        val result = orderMapper.selectPage(Page(page, size), wrapper)
        return PageResult(result.total, result.records)
    }

    private fun generateOrderNo(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val random = (1000..9999).random()
        return "DD${timestamp}${random}"
    }
}
