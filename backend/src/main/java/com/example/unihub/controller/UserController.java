package com.example.unihub.controller;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.User;
import com.example.unihub.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

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

    @PutMapping("/profile")
    public ApiResult<Void> updateProfile(@RequestBody User user, Authentication auth) {
        String username = auth.getName();
        userService.updateProfile(username, user);
        return ApiResult.success(null);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<List<User>> getByRole(@PathVariable String role) {
        return ApiResult.success(userService.getByRole(role));
    }

    @PostMapping("/avatar")
    public ApiResult<String> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication auth) {
        String url = userService.uploadAvatar(auth.getName(), file);
        return ApiResult.success(url);
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('admin')")
    public void exportExcel(@RequestParam(defaultValue = "student") String role, HttpServletResponse resp) throws Exception {
        resp.setContentType("text/csv;charset=utf-8");
        resp.setHeader("Content-Disposition", "attachment;filename=users.csv");
        List<User> list = userService.getByRole(role);
        try (OutputStreamWriter w = new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8)) {
            w.write('\uFEFF'); // BOM for Excel
            w.write("ID,账号,姓名,性别,电话,邮箱,爱好\n");
            for (User u : list) {
                w.write(u.getId() + "," + esc(u.getUsername()) + "," + esc(u.getName()) + "," + esc(u.getGender()) + ","
                        + esc(u.getPhone()) + "," + esc(u.getEmail()) + "," + esc(u.getHobby()) + "\n");
            }
        }
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "student") String role) throws Exception {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (content.startsWith("\uFEFF")) content = content.substring(1);
        String[] lines = content.split("\n");
        int count = 0;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                User u = new User();
                u.setUsername(parts[1].trim());
                u.setName(parts[2].trim());
                u.setGender(parts.length > 3 ? parts[3].trim() : null);
                u.setPhone(parts.length > 4 ? parts[4].trim() : null);
                u.setEmail(parts.length > 5 ? parts[5].trim() : null);
                u.setRole(role);
                userService.add(u);
                count++;
            }
        }
        return ApiResult.success("成功导入 " + count + " 条数据");
    }

    private String esc(String s) { return s != null ? s.replace("\"", "\"\"") : ""; }
}
