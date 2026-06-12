package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.User;
import com.example.unihub.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Page<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(userService.list(page, size, role, keyword));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<User> getById(@PathVariable Integer id) {
        return ApiResult.success(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> add(@RequestBody User user) {
        userService.add(user);
        return ApiResult.success(null);
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> update(@RequestBody User user) {
        userService.update(user);
        return ApiResult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ApiResult.success(null);
    }

    @GetMapping("/role/{role}")
    public ApiResult<List<User>> getByRole(@PathVariable String role) {
        return ApiResult.success(userService.getByRole(role));
    }
}
