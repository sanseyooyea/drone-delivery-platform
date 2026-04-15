package com.drone.delivery.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.drone.delivery.entity.User
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper : BaseMapper<User>
