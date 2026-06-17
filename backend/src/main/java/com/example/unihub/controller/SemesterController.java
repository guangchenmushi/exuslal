package com.example.unihub.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.unihub.entity.Semester;
import com.example.unihub.service.SemesterService;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping
    public ApiResult<Page<Semester>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.success(semesterService.list(page, size));
    }

    @GetMapping("/current")
    public ApiResult<Semester> getCurrent() {
        return ApiResult.success(semesterService.getCurrent());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> add(@RequestBody Semester semester) {
        semesterService.add(semester);
        return ApiResult.success(null);
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> update(@RequestBody Semester semester) {
        semesterService.update(semester);
        return ApiResult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> delete(@PathVariable Integer id) {
        semesterService.delete(id);
        return ApiResult.success(null);
    }
}
