package com.example.unihub.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

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

    @TableField(exist = false)
    private String teacherName;

    @TableField(exist = false)
    private Integer selectedCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
