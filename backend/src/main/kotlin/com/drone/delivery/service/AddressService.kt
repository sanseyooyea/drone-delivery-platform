package com.drone.delivery.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.drone.delivery.common.exception.BizException
import com.drone.delivery.entity.Address
import com.drone.delivery.mapper.AddressMapper
import org.springframework.stereotype.Service

@Service
class AddressService(private val addressMapper: AddressMapper) {

    fun listAddresses(userId: Long): List<Address> =
        addressMapper.selectList(QueryWrapper<Address>().eq("user_id", userId))

    fun createAddress(userId: Long, address: Address): Address {
        address.userId = userId
        if (address.isDefault == 1) {
            clearDefault(userId)
        }
        addressMapper.insert(address)
        return address
    }

    fun updateAddress(userId: Long, id: Long, address: Address) {
        val existing = addressMapper.selectById(id) ?: throw BizException(message = "地址不存在")
        if (existing.userId != userId) throw BizException(message = "无权操作")
        address.id = id
        address.userId = userId
        if (address.isDefault == 1) {
            clearDefault(userId)
        }
        addressMapper.updateById(address)
    }

    fun deleteAddress(userId: Long, id: Long) {
        val existing = addressMapper.selectById(id) ?: throw BizException(message = "地址不存在")
        if (existing.userId != userId) throw BizException(message = "无权操作")
        addressMapper.deleteById(id)
    }

    fun setDefault(userId: Long, id: Long) {
        val existing = addressMapper.selectById(id) ?: throw BizException(message = "地址不存在")
        if (existing.userId != userId) throw BizException(message = "无权操作")
        clearDefault(userId)
        existing.isDefault = 1
        addressMapper.updateById(existing)
    }

    private fun clearDefault(userId: Long) {
        val addresses = addressMapper.selectList(
            QueryWrapper<Address>().eq("user_id", userId).eq("is_default", 1)
        )
        addresses.forEach {
            it.isDefault = 0
            addressMapper.updateById(it)
        }
    }
}
