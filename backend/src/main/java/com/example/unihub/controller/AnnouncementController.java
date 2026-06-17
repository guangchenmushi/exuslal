package com.example.unihub.controller;

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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Announcement;
import com.example.unihub.entity.User;
import com.example.unihub.service.AnnouncementService;
import com.example.unihub.service.UserService;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserService userService;

    public AnnouncementController(AnnouncementService announcementService, UserService userService) {
        this.announcementService = announcementService;
        this.userService = userService;
    }

    @GetMapping("/active")
    public ApiResult<List<Announcement>> getActive(Authentication auth) {
        String role = "all";
        if (auth != null) {
            var authorities = auth.getAuthorities();
            if (authorities != null && !authorities.isEmpty()) {
                role = authorities.iterator().next().getAuthority().replace("ROLE_", "");
            }
        }
        return ApiResult.success(announcementService.getActive(role));
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Page<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.success(announcementService.list(page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> add(@RequestBody Announcement announcement, Authentication auth) {
        // 从用户名查找用户ID，支持数字ID和用户名字符串
        try {
            announcement.setPublisherId(Integer.parseInt(auth.getName()));
        } catch (NumberFormatException e) {
            // auth.getName() 可能是用户名（如"admin"），查数据库获取ID
            User user = userService.getByUsername(auth.getName());
            if (user != null) {
                announcement.setPublisherId(user.getId());
            } else {
                announcement.setPublisherId(1); // 默认管理员ID
            }
        }
        announcementService.add(announcement);
        return ApiResult.success(null);
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> update(@RequestBody Announcement announcement) {
        announcementService.update(announcement);
        return ApiResult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> delete(@PathVariable Integer id) {
        announcementService.delete(id);
        return ApiResult.success(null);
    }
}
