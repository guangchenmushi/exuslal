package com.example.unihub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("grades")
public class Grade {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer studentId;
    private Integer courseId;
    private Double score;
    private String semester;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
