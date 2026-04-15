package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.service.CartService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {

    @GetMapping
    fun listCart(request: HttpServletRequest) =
        Result.ok(data = cartService.listCart(request.getAttribute("userId") as Long))

    @PostMapping
    fun addToCart(
        request: HttpServletRequest,
        @RequestParam productId: Long,
        @RequestParam(defaultValue = "1") quantity: Int
    ): Result<Nothing> {
        cartService.addToCart(request.getAttribute("userId") as Long, productId, quantity)
        return Result.ok("已添加到购物车")
    }

    @PutMapping("/{id}")
    fun updateQuantity(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestParam quantity: Int
    ): Result<Nothing> {
        cartService.updateQuantity(request.getAttribute("userId") as Long, id, quantity)
        return Result.ok("修改成功")
    }

    @DeleteMapping("/{id}")
    fun removeFromCart(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        cartService.removeFromCart(request.getAttribute("userId") as Long, id)
        return Result.ok("删除成功")
    }
}
