package com.example.unihub.controller;

import com.example.unihub.dto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/stats")
    public ApiResult<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("studentCount", 150);
        stats.put("teacherCount", 25);
        stats.put("courseCount", 40);
        stats.put("announcementCount", 12);
        return ApiResult.success(stats);
    }
}
