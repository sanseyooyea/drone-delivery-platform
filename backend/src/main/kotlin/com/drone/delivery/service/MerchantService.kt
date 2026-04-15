package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.drone.delivery.common.Constants
import com.drone.delivery.common.PageResult
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.dto.request.MerchantApplyRequest
import com.drone.delivery.entity.Merchant
import com.drone.delivery.mapper.MerchantMapper
import com.drone.delivery.mapper.UserMapper
import org.springframework.stereotype.Service

@Service
class MerchantService(
    private val merchantMapper: MerchantMapper,
    private val userMapper: UserMapper
) {
    fun apply(userId: Long, req: MerchantApplyRequest) {
        val existing = merchantMapper.selectOne(
            QueryWrapper<Merchant>().eq("user_id", userId)
        )
        if (existing != null) throw BizException(message = "已提交过申请")

        merchantMapper.insert(
            Merchant(
                userId = userId,
                shopName = req.shopName,
                contact = req.contact,
                category = req.category,
                description = req.description
            )
        )
    }

    fun getApplyStatus(userId: Long): Merchant? =
        merchantMapper.selectOne(QueryWrapper<Merchant>().eq("user_id", userId))

    fun listMerchants(page: Long, size: Long): PageResult<Merchant> {
        val result = merchantMapper.selectPage(
            Page(page, size),
            QueryWrapper<Merchant>().orderByDesc("create_time")
        )
        return PageResult(result.total, result.records)
    }

    fun auditMerchant(id: Long, auditStatus: Int, auditRemark: String?) {
        val merchant = merchantMapper.selectById(id) ?: throw BizException(message = "商家不存在")
        merchant.auditStatus = auditStatus
        merchant.auditRemark = auditRemark
        merchantMapper.updateById(merchant)

        if (auditStatus == Constants.AUDIT_APPROVED) {
            val user = userMapper.selectById(merchant.userId)
            user?.let {
                it.role = Constants.ROLE_MERCHANT
                userMapper.updateById(it)
            }
        }
    }
}
