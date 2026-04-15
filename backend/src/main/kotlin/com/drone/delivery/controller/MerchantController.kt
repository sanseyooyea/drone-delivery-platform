package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.dto.request.MerchantApplyRequest
import com.drone.delivery.service.MerchantService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/merchant")
class MerchantController(private val merchantService: MerchantService) {

    @PostMapping("/apply")
    fun apply(request: HttpServletRequest, @Valid @RequestBody req: MerchantApplyRequest): Result<Nothing> {
        merchantService.apply(request.getAttribute("userId") as Long, req)
        return Result.ok("申请已提交")
    }

    @GetMapping("/apply")
    fun getApplyStatus(request: HttpServletRequest) =
        Result.ok(data = merchantService.getApplyStatus(request.getAttribute("userId") as Long))
}
