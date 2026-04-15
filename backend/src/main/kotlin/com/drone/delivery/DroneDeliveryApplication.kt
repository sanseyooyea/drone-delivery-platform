package com.drone.delivery

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("com.drone.delivery.mapper")
class DroneDeliveryApplication

fun main(args: Array<String>) {
    runApplication<DroneDeliveryApplication>(*args)
}
