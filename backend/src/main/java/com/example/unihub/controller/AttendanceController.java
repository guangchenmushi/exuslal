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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Attendance;
import com.example.unihub.entity.Course;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.AttendanceMapper;
import com.example.unihub.mapper.CourseMapper;
import com.example.unihub.mapper.UserMapper;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceMapper attendanceMapper;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    public AttendanceController(AttendanceMapper attendanceMapper, UserMapper userMapper, CourseMapper courseMapper) {
        this.attendanceMapper = attendanceMapper;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public ApiResult<Page<Attendance>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String date) {
        LambdaQueryWrapper<Attendance> q = new LambdaQueryWrapper<>();
        if (courseId != null) q.eq(Attendance::getCourseId, courseId);
        if (date != null && !date.isEmpty()) q.apply("DATE(date) = {0}", date);
        q.orderByDesc(Attendance::getDate);
        Page<Attendance> result = attendanceMapper.selectPage(new Page<>(page, size), q);
        for (Attendance a : result.getRecords()) {
            User u = userMapper.selectById(a.getStudentId());
            if (u != null) a.setStudentName(u.getName());
            Course c = courseMapper.selectById(a.getCourseId());
            if (c != null) a.setCourseName(c.getName());
        }
        return ApiResult.success(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> add(@RequestBody Attendance attendance) {
        // 防重复：同一天同一学生同一课程只能有一条记录
        Long count = attendanceMapper.selectCount(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getStudentId, attendance.getStudentId())
                        .eq(Attendance::getCourseId, attendance.getCourseId())
                        .apply("DATE(date) = {0}", attendance.getDate()));
        if (count > 0) {
            return ApiResult.error(400, "该学生当天已有考勤记录");
        }
        attendanceMapper.insert(attendance);
        return ApiResult.success(null);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> update(@RequestBody Attendance attendance) {
        attendanceMapper.updateById(attendance);
        return ApiResult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin','teacher')")
    public ApiResult<Void> delete(@PathVariable Integer id) {
        attendanceMapper.deleteById(id);
        return ApiResult.success(null);
    }
}
