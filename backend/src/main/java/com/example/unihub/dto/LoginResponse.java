package com.example.unihub.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String name;
}
