package com.example.unihub.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.dto.LoginRequest;
import com.example.unihub.dto.LoginResponse;
import com.example.unihub.dto.RegisterRequest;
import com.example.unihub.service.AuthService;

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

    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody RegisterRequest req) {
        try {
            authService.register(req);
            return ApiResult.success(null);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @PostMapping("/verify-security")
    public ApiResult<String> verifySecurity(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String answer = body.get("answer");
            authService.verifySecurity(username, answer);
            return ApiResult.success("验证通过");
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ApiResult<Void> resetPassword(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String newPassword = body.get("newPassword");
            authService.resetPassword(username, newPassword);
            return ApiResult.success(null);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Void> changePassword(@RequestBody Map<String, String> body, Authentication auth) {
        try {
            String username = auth.getName();
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");
            authService.changePassword(username, oldPassword, newPassword);
            return ApiResult.success(null);
        } catch (RuntimeException e) {
            return ApiResult.error(400, e.getMessage());
        }
    }
}
