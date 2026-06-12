package com.example.unihub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> list(int page, int size, String role, String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> q = new LambdaQueryWrapper<>();
        if (role != null && !role.isEmpty()) {
            q.eq(User::getRole, role);
        }
        if (keyword != null && !keyword.isEmpty()) {
            q.like(User::getName, keyword)
             .or().like(User::getUsername, keyword);
        }
        q.orderByAsc(User::getId);
        return userMapper.selectPage(p, q);
    }

    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.updateById(user);
    }

    public void delete(Integer id) {
        userMapper.deleteById(id);
    }

    public List<User> getByRole(String role) {
        return userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getRole, role));
    }
}
