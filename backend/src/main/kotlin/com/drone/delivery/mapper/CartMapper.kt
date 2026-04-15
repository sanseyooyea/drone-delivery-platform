package com.drone.delivery.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.drone.delivery.entity.Cart
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CartMapper : BaseMapper<Cart>
