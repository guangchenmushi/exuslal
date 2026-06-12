package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Course;
import com.example.unihub.service.CourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ApiResult<Page<Course>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(courseService.list(page, size, keyword));
    }

    @GetMapping("/{id}")
    public ApiResult<Course> getById(@PathVariable Integer id) {
        return ApiResult.success(courseService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> add(@RequestBody Course course) {
        courseService.add(course);
        return ApiResult.success(null);
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> update(@RequestBody Course course) {
        courseService.update(course);
        return ApiResult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ApiResult<Void> delete(@PathVariable Integer id) {
        courseService.delete(id);
        return ApiResult.success(null);
    }
}
