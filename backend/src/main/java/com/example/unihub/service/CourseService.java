package com.example.unihub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Course;
import com.example.unihub.mapper.CourseMapper;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseMapper courseMapper;

    public CourseService(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public Page<Course> list(int page, int size, String keyword) {
        Page<Course> p = new Page<>(page, size);
        LambdaQueryWrapper<Course> q = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            q.like(Course::getName, keyword).or().like(Course::getCode, keyword);
        }
        q.orderByAsc(Course::getId);
        return courseMapper.selectPage(p, q);
    }

    public Course getById(Integer id) {
        return courseMapper.selectById(id);
    }

    public void add(Course course) {
        courseMapper.insert(course);
    }

    public void update(Course course) {
        courseMapper.updateById(course);
    }

    public void delete(Integer id) {
        courseMapper.deleteById(id);
    }
}
