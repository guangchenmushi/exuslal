package com.example.unihub.controller;

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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Homework;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.UserMapper;
import com.example.unihub.service.HomeworkService;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    private final HomeworkService homeworkService;
    private final UserMapper userMapper;
    public HomeworkController(HomeworkService homeworkService, UserMapper userMapper) {
        this.homeworkService = homeworkService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ApiResult<Page<Homework>> list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) Integer courseId, @RequestParam(required = false) Integer teacherId) {
        return ApiResult.success(homeworkService.list(page, size, courseId, teacherId));
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> add(@RequestBody Homework h, Authentication auth) {
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, auth.getName()));
        if (u != null) h.setTeacherId(u.getId());
        homeworkService.add(h);
        return ApiResult.success(null);
    }
    @PutMapping
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> update(@RequestBody Homework h) { homeworkService.update(h); return ApiResult.success(null); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> delete(@PathVariable Integer id) { homeworkService.delete(id); return ApiResult.success(null); }
}
