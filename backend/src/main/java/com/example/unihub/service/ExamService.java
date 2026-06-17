package com.example.unihub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Exam;
import com.example.unihub.mapper.ExamMapper;

@Service
public class ExamService {
    private final ExamMapper examMapper;
    public ExamService(ExamMapper examMapper) { this.examMapper = examMapper; }

    public Page<Exam> list(int page, int size, Integer courseId) {
        Page<Exam> p = new Page<>(page, size);
        LambdaQueryWrapper<Exam> q = new LambdaQueryWrapper<>();
        if (courseId != null) q.eq(Exam::getCourseId, courseId);
        q.orderByDesc(Exam::getExamDate);
        return examMapper.selectPage(p, q);
    }
    @Transactional
    public void add(Exam e) { examMapper.insert(e); }
    @Transactional
    public void update(Exam e) { examMapper.updateById(e); }
    @Transactional
    public void delete(Integer id) { examMapper.deleteById(id); }
}
