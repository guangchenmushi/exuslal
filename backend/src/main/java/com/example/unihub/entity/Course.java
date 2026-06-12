package com.example.unihub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String code;
    private Integer teacherId;
    private Integer credit;
    private Integer maxStudents;
    private String classroom;
    private String schedule;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
