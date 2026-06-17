package com.example.unihub.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.LoginLog;
import com.example.unihub.service.LoginLogService;

@RestController
@RequestMapping("/api/login-logs")
public class LoginLogController {
    private final LoginLogService loginLogService;
    public LoginLogController(LoginLogService loginLogService) { this.loginLogService = loginLogService; }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Page<LoginLog>> list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResult.success(loginLogService.list(page, size));
    }
}
