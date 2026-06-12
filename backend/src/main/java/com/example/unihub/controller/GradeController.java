package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Grade;
import com.example.unihub.service.GradeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ApiResult.success(gradeService.getByStudent(Integer.parseInt(auth.getName())));
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
