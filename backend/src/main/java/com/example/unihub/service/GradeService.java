package com.example.unihub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Grade;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.GradeMapper;
import com.example.unihub.mapper.UserMapper;

@Service
public class GradeService {

    private final GradeMapper gradeMapper;
    private final UserMapper userMapper;

    public GradeService(GradeMapper gradeMapper, UserMapper userMapper) {
        this.gradeMapper = gradeMapper;
        this.userMapper = userMapper;
    }

    public Page<Grade> list(int page, int size, Integer studentId, Integer courseId) {
        Page<Grade> p = new Page<>(page, size);
        LambdaQueryWrapper<Grade> q = new LambdaQueryWrapper<>();
        if (studentId != null) q.eq(Grade::getStudentId, studentId);
        if (courseId != null) q.eq(Grade::getCourseId, courseId);
        q.orderByDesc(Grade::getScore).orderByAsc(Grade::getId);
        return gradeMapper.selectPage(p, q);
    }

    @Transactional
    public void save(Grade grade) {
        gradeMapper.insert(grade);
    }

    @Transactional
    public void update(Grade grade) {
        gradeMapper.updateById(grade);
    }

    @Transactional
    public void delete(Integer id) {
        gradeMapper.deleteById(id);
    }

    public List<Grade> getByStudent(int studentId) {
        return gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .eq(Grade::getStudentId, studentId));
    }

    public List<Grade> getByStudentUsername(String username) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) return List.of();
        return getByStudent(user.getId());
    }
}
