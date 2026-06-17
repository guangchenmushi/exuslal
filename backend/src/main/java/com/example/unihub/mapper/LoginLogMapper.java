package com.example.unihub.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.unihub.entity.LoginLog;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
