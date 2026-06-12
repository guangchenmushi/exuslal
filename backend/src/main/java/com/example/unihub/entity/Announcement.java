package com.example.unihub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("announcements")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String content;
    private String targetRole;   // all, admin, teacher, student
    private Integer publisherId;
    private LocalDateTime publishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
