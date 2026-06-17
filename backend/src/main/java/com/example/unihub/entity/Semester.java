package com.example.unihub.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("semesters")
public class Semester {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;        // 学期名称，如 "2024-2025学年第一学期"
    private String code;        // 学期编码
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;  // 是否为当前学期

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
