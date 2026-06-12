package com.example.unihub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Grade;
import com.example.unihub.mapper.GradeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {

    private final GradeMapper gradeMapper;

    public GradeService(GradeMapper gradeMapper) {
        this.gradeMapper = gradeMapper;
    }

    public Page<Grade> list(int page, int size, Integer studentId, Integer courseId) {
        Page<Grade> p = new Page<>(page, size);
        LambdaQueryWrapper<Grade> q = new LambdaQueryWrapper<>();
        if (studentId != null) q.eq(Grade::getStudentId, studentId);
        if (courseId != null) q.eq(Grade::getCourseId, courseId);
        q.orderByAsc(Grade::getId);
        return gradeMapper.selectPage(p, q);
    }

    public void save(Grade grade) {
        gradeMapper.insert(grade);
    }

    public void update(Grade grade) {
        gradeMapper.updateById(grade);
    }

    public void delete(Integer id) {
        gradeMapper.deleteById(id);
    }

    public List<Grade> getByStudent(Integer studentId) {
        return gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .eq(Grade::getStudentId, studentId));
    }
}
