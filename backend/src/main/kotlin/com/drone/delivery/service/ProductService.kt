package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.drone.delivery.common.PageResult
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.entity.Category
import com.drone.delivery.entity.Merchant
import com.drone.delivery.entity.Product
import com.drone.delivery.mapper.CategoryMapper
import com.drone.delivery.mapper.MerchantMapper
import com.drone.delivery.mapper.ProductMapper
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productMapper: ProductMapper,
    private val categoryMapper: CategoryMapper,
    private val merchantMapper: MerchantMapper
) {
    fun listProducts(page: Long, size: Long, keyword: String?, categoryId: Long?): PageResult<Product> {
        val wrapper = QueryWrapper<Product>()
            .eq("status", 1)
            .like(!keyword.isNullOrBlank(), "name", keyword)
            .eq(categoryId != null, "category_id", categoryId)
            .orderByDesc("create_time")

        val result = productMapper.selectPage(Page(page, size), wrapper)
        return PageResult(result.total, result.records)
    }

    fun getProduct(id: Long): Product =
        productMapper.selectById(id) ?: throw BizException(message = "商品不存在")

    fun listCategories(): List<Category> =
        categoryMapper.selectList(QueryWrapper<Category>().orderByAsc("sort_order"))

    fun getMerchantId(userId: Long): Long {
        val merchant = merchantMapper.selectOne(
            QueryWrapper<Merchant>().eq("user_id", userId)
        ) ?: throw BizException(message = "商家信息不存在")
        return merchant.id!!
    }

    fun listMerchantProducts(merchantId: Long, page: Long, size: Long): PageResult<Product> {
        val wrapper = QueryWrapper<Product>()
            .eq("merchant_id", merchantId)
            .orderByDesc("create_time")
        val result = productMapper.selectPage(Page(page, size), wrapper)
        return PageResult(result.total, result.records)
    }

    fun createProduct(merchantId: Long, product: Product): Product {
        product.merchantId = merchantId
        productMapper.insert(product)
        return product
    }

    fun updateProduct(merchantId: Long, id: Long, product: Product) {
        val existing = productMapper.selectById(id) ?: throw BizException(message = "商品不存在")
        if (existing.merchantId != merchantId) throw BizException(message = "无权操作")
        product.id = id
        product.merchantId = merchantId
        productMapper.updateById(product)
    }

    fun deleteProduct(merchantId: Long, id: Long) {
        val existing = productMapper.selectById(id) ?: throw BizException(message = "商品不存在")
        if (existing.merchantId != merchantId) throw BizException(message = "无权操作")
        productMapper.deleteById(id)
    }

    fun toggleProductStatus(merchantId: Long, id: Long, status: Int) {
        val existing = productMapper.selectById(id) ?: throw BizException(message = "商品不存在")
        if (existing.merchantId != merchantId) throw BizException(message = "无权操作")
        existing.status = status
        productMapper.updateById(existing)
    }
}
