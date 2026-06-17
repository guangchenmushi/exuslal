package com.example.unihub.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;
    private String password;
    private String role;         // admin, teacher, student
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String hobby;
    private String avatar;
    private String stuId;         // 学号
    private String securityQuestion;
    private String securityAnswer;
    private Integer classId;       // 班级ID

    @TableField(exist = false)
    private String className;      // 班级名称（非数据库字段）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
