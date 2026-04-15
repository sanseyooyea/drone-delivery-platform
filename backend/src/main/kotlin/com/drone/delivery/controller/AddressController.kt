package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.entity.Address
import com.drone.delivery.service.AddressService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/addresses")
class AddressController(private val addressService: AddressService) {

    @GetMapping
    fun listAddresses(request: HttpServletRequest) =
        Result.ok(data = addressService.listAddresses(request.getAttribute("userId") as Long))

    @PostMapping
    fun createAddress(request: HttpServletRequest, @RequestBody address: Address) =
        Result.ok(data = addressService.createAddress(request.getAttribute("userId") as Long, address))

    @PutMapping("/{id}")
    fun updateAddress(request: HttpServletRequest, @PathVariable id: Long, @RequestBody address: Address): Result<Nothing> {
        addressService.updateAddress(request.getAttribute("userId") as Long, id, address)
        return Result.ok("修改成功")
    }

    @DeleteMapping("/{id}")
    fun deleteAddress(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        addressService.deleteAddress(request.getAttribute("userId") as Long, id)
        return Result.ok("删除成功")
    }

    @PutMapping("/{id}/default")
    fun setDefault(request: HttpServletRequest, @PathVariable id: Long): Result<Nothing> {
        addressService.setDefault(request.getAttribute("userId") as Long, id)
        return Result.ok("设置成功")
    }
}
