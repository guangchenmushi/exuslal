package com.example.unihub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.unihub.dto.LoginRequest;
import com.example.unihub.dto.LoginResponse;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.UserMapper;
import com.example.unihub.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUsername(user.getUsername());
        resp.setRole(user.getRole());
        resp.setName(user.getName());
        return resp;
    }
}
