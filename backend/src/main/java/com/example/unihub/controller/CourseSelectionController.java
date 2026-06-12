package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.CourseSelection;
import com.example.unihub.service.CourseSelectionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/selections")
public class CourseSelectionController {

    private final CourseSelectionService selectionService;

    public CourseSelectionController(CourseSelectionService selectionService) {
        this.selectionService = selectionService;
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
        selectionService.selectCourse(Integer.parseInt(auth.getName()), courseId);
        return ApiResult.success(null);
    }

    @DeleteMapping("/drop/{courseId}")
    @PreAuthorize("hasRole('student')")
    public ApiResult<Void> drop(@PathVariable Integer courseId, Authentication auth) {
        selectionService.dropCourse(Integer.parseInt(auth.getName()), courseId);
        return ApiResult.success(null);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('student')")
    public ApiResult<List<CourseSelection>> my(Authentication auth) {
        return ApiResult.success(selectionService.getByStudent(Integer.parseInt(auth.getName())));
    }
}
