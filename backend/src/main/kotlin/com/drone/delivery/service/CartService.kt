package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.dto.response.CartItemResponse
import com.drone.delivery.entity.Cart
import com.drone.delivery.mapper.CartMapper
import com.drone.delivery.mapper.ProductMapper
import org.springframework.stereotype.Service

@Service
class CartService(
    private val cartMapper: CartMapper,
    private val productMapper: ProductMapper
) {

    fun listCart(userId: Long): List<CartItemResponse> {
        val carts = cartMapper.selectList(QueryWrapper<Cart>().eq("user_id", userId))
        return carts.mapNotNull { cart ->
            val product = productMapper.selectById(cart.productId) ?: return@mapNotNull null
            CartItemResponse(
                id = cart.id!!,
                productId = product.id!!,
                productName = product.name,
                productPrice = product.price,
                image = product.image,
                quantity = cart.quantity,
                merchantId = product.merchantId
            )
        }
    }

    fun addToCart(userId: Long, productId: Long, quantity: Int) {
        val existing = cartMapper.selectOne(
            QueryWrapper<Cart>()
                .eq("user_id", userId)
                .eq("product_id", productId)
        )
        if (existing != null) {
            existing.quantity += quantity
            cartMapper.updateById(existing)
        } else {
            cartMapper.insert(Cart(userId = userId, productId = productId, quantity = quantity))
        }
    }

    fun updateQuantity(userId: Long, id: Long, quantity: Int) {
        val cart = cartMapper.selectById(id) ?: throw BizException(message = "购物车项不存在")
        if (cart.userId != userId) throw BizException(message = "无权操作")
        cart.quantity = quantity
        cartMapper.updateById(cart)
    }

    fun removeFromCart(userId: Long, id: Long) {
        val cart = cartMapper.selectById(id) ?: throw BizException(message = "购物车项不存在")
        if (cart.userId != userId) throw BizException(message = "无权操作")
        cartMapper.deleteById(id)
    }

    fun clearCartItems(userId: Long, productIds: List<Long>) {
        cartMapper.delete(
            QueryWrapper<Cart>()
                .eq("user_id", userId)
                .`in`("product_id", productIds)
        )
    }
}
