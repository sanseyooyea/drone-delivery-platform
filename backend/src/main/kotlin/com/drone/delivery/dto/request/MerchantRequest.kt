package com.drone.delivery.dto.request

import jakarta.validation.constraints.NotBlank

data class MerchantApplyRequest(
    @field:NotBlank(message = "店铺名称不能为空")
    val shopName: String,
    val contact: String? = null,
    val category: String? = null,
    val description: String? = null
)

data class AuditMerchantRequest(
    val auditStatus: Int,
    val auditRemark: String? = null
)
