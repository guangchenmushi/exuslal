package com.example.unihub.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.CourseSelection;
import com.example.unihub.mapper.CourseSelectionMapper;

@Service
public class CourseSelectionService {

    private final CourseSelectionMapper selectionMapper;
    private final StringRedisTemplate redisTemplate;

    public CourseSelectionService(CourseSelectionMapper selectionMapper,
                                   StringRedisTemplate redisTemplate) {
        this.selectionMapper = selectionMapper;
        this.redisTemplate = redisTemplate;
    }

    public Page<CourseSelection> list(int page, int size, Integer studentId, Integer courseId) {
        Page<CourseSelection> p = new Page<>(page, size);
        LambdaQueryWrapper<CourseSelection> q = new LambdaQueryWrapper<>();
        if (studentId != null) q.eq(CourseSelection::getStudentId, studentId);
        if (courseId != null) q.eq(CourseSelection::getCourseId, courseId);
        q.orderByDesc(CourseSelection::getSelectedAt);
        return selectionMapper.selectPage(p, q);
    }

    @Transactional
    public boolean selectCourse(int studentId, int courseId) {
        String lockKey = "lock:course:" + courseId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofSeconds(5));
        if (Boolean.FALSE.equals(locked)) {
            throw new RuntimeException("选课系统繁忙，请稍后重试");
        }
        try {
            Long count = selectionMapper.selectCount(
                    new LambdaQueryWrapper<CourseSelection>()
                            .eq(CourseSelection::getStudentId, studentId)
                            .eq(CourseSelection::getCourseId, courseId));
            if (count > 0) {
                throw new RuntimeException("已选择该课程");
            }
            CourseSelection cs = new CourseSelection();
            cs.setStudentId(studentId);
            cs.setCourseId(courseId);
            selectionMapper.insert(cs);
            return true;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public void dropCourse(int studentId, int courseId) {
        selectionMapper.delete(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getStudentId, studentId)
                        .eq(CourseSelection::getCourseId, courseId));
    }

    public List<CourseSelection> getByStudent(int studentId) {
        return selectionMapper.selectList(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getStudentId, studentId));
    }
}
