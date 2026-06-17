package com.example.unihub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Homework;
import com.example.unihub.mapper.HomeworkMapper;

@Service
public class HomeworkService {
    private final HomeworkMapper homeworkMapper;
    public HomeworkService(HomeworkMapper homeworkMapper) { this.homeworkMapper = homeworkMapper; }

    public Page<Homework> list(int page, int size, Integer courseId, Integer teacherId) {
        Page<Homework> p = new Page<>(page, size);
        LambdaQueryWrapper<Homework> q = new LambdaQueryWrapper<>();
        if (courseId != null) q.eq(Homework::getCourseId, courseId);
        if (teacherId != null) q.eq(Homework::getTeacherId, teacherId);
        q.orderByDesc(Homework::getCreatedAt);
        return homeworkMapper.selectPage(p, q);
    }
    @Transactional
    public void add(Homework h) { homeworkMapper.insert(h); }
    @Transactional
    public void update(Homework h) { homeworkMapper.updateById(h); }
    @Transactional
    public void delete(Integer id) { homeworkMapper.deleteById(id); }
}
