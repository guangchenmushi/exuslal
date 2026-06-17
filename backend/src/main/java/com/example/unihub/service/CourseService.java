package com.example.unihub.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Course;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.CourseMapper;
import com.example.unihub.mapper.CourseSelectionMapper;
import com.example.unihub.mapper.UserMapper;

@Service
public class CourseService {

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final CourseSelectionMapper selectionMapper;

    public CourseService(CourseMapper courseMapper, UserMapper userMapper, CourseSelectionMapper selectionMapper) {
        this.courseMapper = courseMapper;
        this.userMapper = userMapper;
        this.selectionMapper = selectionMapper;
    }

    public Page<Course> list(int page, int size, String keyword) {
        Page<Course> p = new Page<>(page, size);
        LambdaQueryWrapper<Course> q = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            q.like(Course::getName, keyword).or().like(Course::getCode, keyword);
        }
        q.orderByAsc(Course::getId);
        Page<Course> result = courseMapper.selectPage(p, q);
        // 填充教师姓名和选课人数
        for (Course c : result.getRecords()) {
            if (c.getTeacherId() != null) {
                User teacher = userMapper.selectById(c.getTeacherId());
                if (teacher != null) c.setTeacherName(teacher.getName());
            }
            Long count = selectionMapper.selectCount(
                    new LambdaQueryWrapper<com.example.unihub.entity.CourseSelection>()
                            .eq(com.example.unihub.entity.CourseSelection::getCourseId, c.getId()));
            c.setSelectedCount(count.intValue());
        }
        return result;
    }

    public Course getById(Integer id) {
        return courseMapper.selectById(id);
    }

    @Transactional
    public void add(Course course) {
        courseMapper.insert(course);
    }

    @Transactional
    public void update(Course course) {
        courseMapper.updateById(course);
    }

    @Transactional
    public void delete(Integer id) {
        courseMapper.deleteById(id);
    }

    public void fixClassroomAndSchedule() {
        Map<Integer, String[]> fix = new HashMap<>();
        fix.put(1, new String[]{"教学楼A101", "周一 1-2节"});
        fix.put(2, new String[]{"教学楼A102", "周一 3-4节"});
        fix.put(3, new String[]{"教学楼A103", "周二 1-2节"});
        fix.put(4, new String[]{"教学楼A104", "周二 5-6节"});
        fix.put(5, new String[]{"教学楼B101", "周三 3-4节"});
        fix.put(6, new String[]{"教学楼B102", "周三 1-2节"});
        fix.put(7, new String[]{"教学楼B103", "周四 1-2节"});
        fix.put(8, new String[]{"教学楼B104", "周四 3-4节"});
        fix.put(9, new String[]{"教学楼C101", "周五 5-6节"});
        fix.put(10, new String[]{"教学楼C102", "周五 5-6节"});
        fix.put(11, new String[]{"教学楼C103", "周三 3-4节"});
        fix.put(12, new String[]{"教学楼C201", "周四 5-6节"});
        fix.put(13, new String[]{"教学楼C202", "周二 1-2节"});
        fix.put(14, new String[]{"教学楼C203", "周二 7-8节"});
        fix.put(15, new String[]{"教学楼A201", "周三 7-8节"});
        fix.put(16, new String[]{"教学楼C301", "周一 5-6节"});
        fix.put(17, new String[]{"教学楼C302", "周二 7-8节"});
        fix.put(18, new String[]{"教学楼C101", "周二 1-2节"});
        fix.put(19, new String[]{"教学楼C102", "周四 3-4节"});
        fix.put(20, new String[]{"教学楼D101", "周五 5-6节"});
        fix.put(21, new String[]{"教学楼D102", "周三 1-2节"});
        fix.put(22, new String[]{"教学楼D103", "周五 3-4节"});
        fix.put(23, new String[]{"教学楼E101", "周四 5-6节"});
        fix.put(24, new String[]{"教学楼E102", "周一 7-8节"});
        fix.put(25, new String[]{"教学楼E103", "周三 5-6节"});
        fix.put(26, new String[]{"教学楼F101", "周二 3-4节"});
        fix.put(27, new String[]{"教学楼F102", "周四 1-2节"});
        fix.put(28, new String[]{"教学楼F103", "周五 1-2节"});
        fix.put(29, new String[]{"体育馆", "周一 3-4节"});
        fix.put(30, new String[]{"教学楼A301", "周三 5-6节"});

        for (Map.Entry<Integer, String[]> e : fix.entrySet()) {
            Course c = courseMapper.selectById(e.getKey());
            if (c != null) {
                c.setClassroom(e.getValue()[0]);
                c.setSchedule(e.getValue()[1]);
                courseMapper.updateById(c);
            }
        }
    }
}
