package com.example.unihub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer studentId;
    private Integer courseId;
    private String status;   // present, absent, late, leave
    private LocalDateTime date;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
