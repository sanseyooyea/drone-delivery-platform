package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.drone.delivery.common.PageResult
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.dto.response.DroneLogResponse
import com.drone.delivery.entity.Drone
import com.drone.delivery.entity.DroneLog
import com.drone.delivery.mapper.*
import org.springframework.stereotype.Service

@Service
class DroneService(
    private val droneMapper: DroneMapper,
    private val droneLogMapper: DroneLogMapper,
    private val orderMapper: OrderMapper,
    private val addressMapper: AddressMapper
) {
    fun listDrones(page: Long, size: Long): PageResult<Drone> {
        val result = droneMapper.selectPage(Page(page, size), QueryWrapper<Drone>())
        return PageResult(result.total, result.records)
    }

    fun createDrone(drone: Drone): Drone {
        droneMapper.insert(drone)
        return drone
    }

    fun updateDrone(id: Long, drone: Drone) {
        droneMapper.selectById(id) ?: throw BizException(message = "无人机不存在")
        drone.id = id
        droneMapper.updateById(drone)
    }

    fun getDroneLogs(droneId: Long, page: Long, size: Long): PageResult<DroneLogResponse> {
        val result = droneLogMapper.selectPage(
            Page(page, size),
            QueryWrapper<DroneLog>().eq("drone_id", droneId).orderByDesc("start_time")
        )

        val list = result.records.map { log ->
            val order = orderMapper.selectById(log.orderId)
            val address = order?.addressId?.let { addressMapper.selectById(it) }
            DroneLogResponse(
                id = log.id!!,
                droneId = log.droneId,
                orderId = log.orderId,
                orderNo = order?.orderNo ?: "",
                deliveryAddress = address?.address,
                startTime = log.startTime,
                endTime = log.endTime,
                status = log.status
            )
        }

        return PageResult(result.total, list)
    }
}
