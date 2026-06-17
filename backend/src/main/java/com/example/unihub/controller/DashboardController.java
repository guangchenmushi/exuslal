package com.example.unihub.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.unihub.dto.ApiResult;
import com.example.unihub.entity.Attendance;
import com.example.unihub.entity.Course;
import com.example.unihub.entity.CourseSelection;
import com.example.unihub.entity.Grade;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.AnnouncementMapper;
import com.example.unihub.mapper.AttendanceMapper;
import com.example.unihub.mapper.CourseMapper;
import com.example.unihub.mapper.CourseSelectionMapper;
import com.example.unihub.mapper.GradeMapper;
import com.example.unihub.mapper.UserMapper;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final AnnouncementMapper announcementMapper;
    private final AttendanceMapper attendanceMapper;
    private final GradeMapper gradeMapper;
    private final CourseSelectionMapper selectionMapper;

    public DashboardController(UserMapper userMapper, CourseMapper courseMapper,
            AnnouncementMapper announcementMapper, AttendanceMapper attendanceMapper,
            GradeMapper gradeMapper, CourseSelectionMapper selectionMapper) {
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.announcementMapper = announcementMapper;
        this.attendanceMapper = attendanceMapper;
        this.gradeMapper = gradeMapper;
        this.selectionMapper = selectionMapper;
    }

    @GetMapping("/stats")
    public ApiResult<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("studentCount", userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "student")));
        stats.put("teacherCount", userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "teacher")));
        stats.put("courseCount", courseMapper.selectCount(null));
        stats.put("announcementCount", announcementMapper.selectCount(null));
        return ApiResult.success(stats);
    }

    @GetMapping("/charts")
    public ApiResult<Map<String, Object>> charts() {
        Map<String, Object> data = new HashMap<>();

        // 1. 课程选课人数分布 (Top 5 热门课程)
        List<Course> allCourses = courseMapper.selectList(null);
        List<Map<String, Object>> courseSelectDist = new ArrayList<>();
        for (Course c : allCourses) {
            Long count = selectionMapper.selectCount(
                    new LambdaQueryWrapper<CourseSelection>().eq(CourseSelection::getCourseId, c.getId()));
            courseSelectDist.add(mapOf("name", c.getName(), "value", count.intValue()));
        }
        courseSelectDist.sort((a, b) -> ((Integer) b.get("value")).compareTo((Integer) a.get("value")));
        data.put("gradeDistribution", courseSelectDist.size() > 8 ?
                courseSelectDist.subList(0, 8) : courseSelectDist);

        // 2. 学期平均成绩趋势 (真实数据)
        List<Grade> allGrades = gradeMapper.selectList(null);
        Map<String, List<Double>> semesterScores = new LinkedHashMap<>();
        for (Grade g : allGrades) {
            if (g.getScore() == null) continue;
            String sem = g.getSemester() != null ? g.getSemester() : "未知";
            semesterScores.computeIfAbsent(sem, k -> new ArrayList<>()).add(g.getScore());
        }
        List<Map<String, Object>> gradeTrend = new ArrayList<>();
        for (Map.Entry<String, List<Double>> e : semesterScores.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            gradeTrend.add(mapOf("semester", e.getKey(), "avgScore", Math.round(avg * 10) / 10.0));
        }
        if (gradeTrend.isEmpty()) {
            gradeTrend.add(mapOf("semester", "暂无数据", "avgScore", 0));
        }
        data.put("gradeTrend", gradeTrend);

        // 3. 课程分类统计 (基于课程编码前缀)
        Map<String, Integer> catMap = new LinkedHashMap<>();
        catMap.put("理工类", 0); catMap.put("文科类", 0);
        catMap.put("体育类", 0); catMap.put("艺术类", 0); catMap.put("通识类", 0);
        for (Course c : allCourses) {
            String code = c.getCode() != null ? c.getCode() : "";
            if (code.startsWith("CS") || code.startsWith("MATH") || code.startsWith("EE") || code.startsWith("PHY"))
                catMap.merge("理工类", 1, Integer::sum);
            else if (code.startsWith("ENG"))
                catMap.merge("文科类", 1, Integer::sum);
            else if (code.startsWith("PE"))
                catMap.merge("体育类", 1, Integer::sum);
            else
                catMap.merge("通识类", 1, Integer::sum);
        }
        List<Map<String, Object>> courseCats = new ArrayList<>();
        for (Map.Entry<String, Integer> e : catMap.entrySet()) {
            courseCats.add(mapOf("name", e.getKey(), "value", e.getValue()));
        }
        data.put("courseCategories", courseCats);

        // 4. 考勤率 (真实数据)
        long totalAtt = attendanceMapper.selectCount(null);
        long present = attendanceMapper.selectCount(
                new LambdaQueryWrapper<Attendance>().eq(Attendance::getStatus, "present"));
        int rate = totalAtt > 0 ? (int)(present * 100 / totalAtt) : 0;
        data.put("attendanceRate", rate);

        // 5. 用户角色分布 (真实数据)
        long studentCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "student"));
        long teacherCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "teacher"));
        data.put("roleDistribution", Arrays.asList(
            mapOf("name", "学生", "value", studentCount),
            mapOf("name", "教师", "value", teacherCount),
            mapOf("name", "管理员", "value", 1L)
        ));

        return ApiResult.success(data);
    }

    private Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> m = new HashMap<>(); m.put(k1, v1); m.put(k2, v2); return m;
    }
}
