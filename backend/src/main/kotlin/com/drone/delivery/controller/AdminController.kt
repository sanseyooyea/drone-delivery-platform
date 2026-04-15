package com.drone.delivery.controller

import com.drone.delivery.common.Result
import com.drone.delivery.dto.request.AuditMerchantRequest
import com.drone.delivery.entity.Drone
import com.drone.delivery.service.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService,
    private val merchantService: MerchantService,
    private val droneService: DroneService,
    private val orderService: OrderService,
    private val chatService: ChatService
) {
    // 用户管理
    @GetMapping("/users")
    fun listUsers(
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ) = Result.ok(data = adminService.listUsers(page, size))

    @PutMapping("/users/{id}/status")
    fun toggleUserStatus(@PathVariable id: Long, @RequestParam status: Int): Result<Nothing> {
        adminService.toggleUserStatus(id, status)
        return Result.ok("操作成功")
    }

    // 商家管理
    @GetMapping("/merchants")
    fun listMerchants(
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ) = Result.ok(data = merchantService.listMerchants(page, size))

    @PutMapping("/merchants/{id}/audit")
    fun auditMerchant(@PathVariable id: Long, @RequestBody req: AuditMerchantRequest): Result<Nothing> {
        merchantService.auditMerchant(id, req.auditStatus, req.auditRemark)
        return Result.ok("审核完成")
    }

    // 无人机管理
    @GetMapping("/drones")
    fun listDrones(
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ) = Result.ok(data = droneService.listDrones(page, size))

    @PostMapping("/drones")
    fun createDrone(@RequestBody drone: Drone) =
        Result.ok(data = droneService.createDrone(drone))

    @PutMapping("/drones/{id}")
    fun updateDrone(@PathVariable id: Long, @RequestBody drone: Drone): Result<Nothing> {
        droneService.updateDrone(id, drone)
        return Result.ok("修改成功")
    }

    @GetMapping("/drones/{id}/logs")
    fun getDroneLogs(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ) = Result.ok(data = droneService.getDroneLogs(id, page, size))

    // 订单管理
    @GetMapping("/orders")
    fun listOrders(
        @RequestParam(required = false) status: Int?,
        @RequestParam(required = false) merchantId: Long?,
        @RequestParam(defaultValue = "1") page: Long,
        @RequestParam(defaultValue = "10") size: Long
    ) = Result.ok(data = orderService.listAllOrders(status, merchantId, page, size))

    // 客服管理
    @GetMapping("/chat/sessions")
    fun listActiveSessions() =
        Result.ok(data = chatService.listActiveSessions())

    @PutMapping("/chat/sessions/{id}/close")
    fun closeSession(@PathVariable id: Long): Result<Nothing> {
        chatService.closeSession(id)
        return Result.ok("会话已关闭")
    }

    // 平台数据
    @GetMapping("/stats")
    fun getStats() = Result.ok(data = adminService.getStats())
}
