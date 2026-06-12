package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.dto.LoginRequest;
import com.example.unihub.dto.LoginResponse;
import com.example.unihub.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestBody LoginRequest req) {
        try {
            return ApiResult.success(authService.login(req));
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }
}
