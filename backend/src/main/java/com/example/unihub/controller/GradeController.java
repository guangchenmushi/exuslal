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
import com.example.unihub.entity.Grade;
import com.example.unihub.service.GradeService;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Page<Grade>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer courseId) {
        return ApiResult.success(gradeService.list(page, size, studentId, courseId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('student')")
    public ApiResult<List<Grade>> my(Authentication auth) {
        String username = auth.getName();
        return ApiResult.success(gradeService.getByStudentUsername(username));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> save(@RequestBody Grade grade) {
        gradeService.save(grade);
        return ApiResult.success(null);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> update(@RequestBody Grade grade) {
        gradeService.update(grade);
        return ApiResult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> delete(@PathVariable Integer id) {
        gradeService.delete(id);
        return ApiResult.success(null);
    }
}
