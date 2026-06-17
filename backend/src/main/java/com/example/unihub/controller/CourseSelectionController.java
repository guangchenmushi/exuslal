package com.example.unihub.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.CourseSelection;
import com.example.unihub.service.CourseSelectionService;
import com.example.unihub.service.UserService;

@RestController
@RequestMapping("/api/selections")
public class CourseSelectionController {

    private final CourseSelectionService selectionService;
    private final UserService userService;

    public CourseSelectionController(CourseSelectionService selectionService, UserService userService) {
        this.selectionService = selectionService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Page<CourseSelection>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer courseId) {
        return ApiResult.success(selectionService.list(page, size, studentId, courseId));
    }

    @PostMapping("/select/{courseId}")
    @PreAuthorize("hasRole('student')")
    public ApiResult<Void> select(@PathVariable Integer courseId, Authentication auth) {
        var user = userService.getByUsername(auth.getName());
        if (user == null) return ApiResult.error(400, "用户不存在");
        selectionService.selectCourse(user.getId(), courseId);
        return ApiResult.success(null);
    }

    @DeleteMapping("/drop/{courseId}")
    @PreAuthorize("hasRole('student')")
    public ApiResult<Void> drop(@PathVariable Integer courseId, Authentication auth) {
        var user = userService.getByUsername(auth.getName());
        if (user == null) return ApiResult.error(400, "用户不存在");
        selectionService.dropCourse(user.getId(), courseId);
        return ApiResult.success(null);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('student')")
    public ApiResult<List<CourseSelection>> my(Authentication auth) {
        var user = userService.getByUsername(auth.getName());
        if (user == null) return ApiResult.error(400, "用户不存在");
        return ApiResult.success(selectionService.getByStudent(user.getId()));
    }
}
