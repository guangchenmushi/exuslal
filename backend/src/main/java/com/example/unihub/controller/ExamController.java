package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Exam;
import com.example.unihub.service.ExamService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;
    public ExamController(ExamService examService) { this.examService = examService; }

    @GetMapping
    public ApiResult<Page<Exam>> list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) Integer courseId) {
        return ApiResult.success(examService.list(page, size, courseId));
    }
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> add(@RequestBody Exam e) { examService.add(e); return ApiResult.success(null); }
    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> update(@RequestBody Exam e) { examService.update(e); return ApiResult.success(null); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> delete(@PathVariable Integer id) { examService.delete(id); return ApiResult.success(null); }
}
