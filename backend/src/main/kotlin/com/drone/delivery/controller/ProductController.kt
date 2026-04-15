package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.entity.Product
import com.drone.delivery.service.ProductService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ProductController(private val productService: ProductService) {

    @GetMapping("/products")
    fun listProducts(
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) categoryId: Long?
    ) = Result.ok(data = productService.listProducts(page, size, keyword, categoryId))

    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: Long) =
        Result.ok(data = productService.getProduct(id))

    @GetMapping("/categories")
    fun listCategories() =
        Result.ok(data = productService.listCategories())

    // 商家商品管理
    @GetMapping("/merchant/products")
    fun listMerchantProducts(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ): Result<*> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        return Result.ok(data = productService.listMerchantProducts(merchantId, page, size))
    }

    @PostMapping("/merchant/products")
    fun createProduct(request: HttpServletRequest, @RequestBody product: Product): Result<*> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        return Result.ok(data = productService.createProduct(merchantId, product))
    }

    @PutMapping("/merchant/products/{id}")
    fun updateProduct(request: HttpServletRequest, @PathVariable id: Long, @RequestBody product: Product): Result<Nothing> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        productService.updateProduct(merchantId, id, product)
        return Result.ok("修改成功")
    }

    @DeleteMapping("/merchant/products/{id}")
    fun deleteProduct(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        productService.deleteProduct(merchantId, id)
        return Result.ok("删除成功")
    }

    @PutMapping("/merchant/products/{id}/status")
    fun toggleStatus(
        request: HttpServletRequest,
        @PathVariable id: Long,
        @RequestParam status: Int
    ): Result<Nothing> {
        val merchantId = productService.getMerchantId(request.getAttribute("userId") as Long)
        productService.toggleProductStatus(merchantId, id, status)
        return Result.ok("操作成功")
    }
}
