package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Announcement;
import com.example.unihub.service.AnnouncementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/active")
    public ApiResult<List<Announcement>> getActive(Authentication auth) {
        String role = auth != null ?
                auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "") : "all";
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
        announcement.setPublisherId(Integer.parseInt(auth.getName()));
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
