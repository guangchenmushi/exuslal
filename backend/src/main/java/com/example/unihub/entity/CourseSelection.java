package com.example.unihub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_selections")
public class CourseSelection {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer studentId;
    private Integer courseId;
    private LocalDateTime selectedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
