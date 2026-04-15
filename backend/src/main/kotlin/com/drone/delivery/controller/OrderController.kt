package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.dto.request.CreateOrderRequest
import com.drone.delivery.dto.request.RejectOrderRequest
import com.drone.delivery.mapper.AddressMapper
import com.drone.delivery.service.OrderService
import com.drone.delivery.service.ProductService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class OrderController(
    private val orderService: OrderService,
    private val productService: ProductService,
    private val addressMapper: AddressMapper
) {
    // 用户端
    @PostMapping("/orders")
    fun createOrder(request: HttpServletRequest, @Valid @RequestBody req: CreateOrderRequest): Result<*> {
        val userId = request.getAttribute("userId") as Long
        val items = req.items.map { it.productId to it.quantity }
        val order = orderService.createOrder(userId, req.merchantId, req.addressId, items, req.remark)
        return Result.ok(data = order)
    }

    @GetMapping("/orders")
    fun listOrders(
        request: HttpServletRequest,
        @RequestParam(required = false) status: Int?,
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ) = Result.ok(data = orderService.listUserOrders(request.getAttribute("userId") as Long, status, page, size))

    @GetMapping("/orders/{id}")
    fun getOrder(@PathVariable id: Long): Result<*> {
        val order = orderService.getOrder(id)
        val items = orderService.getOrderItems(id)
        val address = order.addressId?.let { addressMapper.selectById(it) }
        return Result.ok(data = mapOf("order" to order, "items" to items, "address" to address))
    }

    @PutMapping("/orders/{id}/cancel")
    fun cancelOrder(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        orderService.cancelOrder(request.getAttribute("userId") as Long, id)
        return Result.ok("取消成功")
    }

    @PutMapping("/orders/{id}/confirm")
    fun confirmReceive(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        orderService.confirmReceive(request.getAttribute("userId") as Long, id)
        return Result.ok("确认收货成功")
    }

    // 商家端
    @GetMapping("/merchant/orders")
    fun listMerchantOrders(
        request: HttpServletRequest,
        @RequestParam(required = false) status: Int?,
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ): Result<*> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        return Result.ok(data = orderService.listMerchantOrders(merchantId, status, page, size))
    }

    @PutMapping("/merchant/orders/{id}/accept")
    fun acceptOrder(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        orderService.acceptOrder(merchantId, id)
        return Result.ok("接单成功")
    }

    @PutMapping("/merchant/orders/{id}/reject")
    fun rejectOrder(request: HttpServletRequest, @PathVariable id: Long, @RequestBody req: RejectOrderRequest): Result<Nothing> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        orderService.rejectOrder(merchantId, id, req.reason)
        return Result.ok("已拒单")
    }

    @PutMapping("/merchant/orders/{id}/ready")
    fun markReady(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        orderService.markReady(merchantId, id)
        return Result.ok("已标记备货完成，无人机配送中")
    }
}
