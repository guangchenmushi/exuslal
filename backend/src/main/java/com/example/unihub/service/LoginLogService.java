package com.example.unihub.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.LoginLog;
import com.example.unihub.mapper.LoginLogMapper;

@Service
public class LoginLogService {
    private final LoginLogMapper loginLogMapper;
    public LoginLogService(LoginLogMapper loginLogMapper) { this.loginLogMapper = loginLogMapper; }

    public Page<LoginLog> list(int page, int size) {
        return loginLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<LoginLog>().orderByDesc(LoginLog::getLoginTime));
    }
}
