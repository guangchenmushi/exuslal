package com.example.unihub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
