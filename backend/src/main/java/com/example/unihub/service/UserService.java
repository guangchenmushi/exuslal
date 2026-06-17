package com.example.unihub.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.User;
import com.example.unihub.mapper.UserMapper;

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

    @Transactional
    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    @Transactional
    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.updateById(user);
    }

    @Transactional
    public void delete(Integer id) {
        userMapper.deleteById(id);
    }

    @Transactional
    public void updateProfile(String username, User updated) {
        User existing = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (existing == null) throw new RuntimeException("用户不存在");
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getGender() != null) existing.setGender(updated.getGender());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getHobby() != null) existing.setHobby(updated.getHobby());
        // 密码字段不设为null，避免覆盖数据库中的值
        // 只复制需要更新的字段
        userMapper.updateById(existing);
    }

    public User getByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    public List<User> getByRole(String role) {
        return userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getRole, role));
    }

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Transactional
    public String uploadAvatar(String username, MultipartFile file) {
        // 文件大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("头像文件大小不能超过 5MB");
        }
        // 文件类型校验
        String originalName = file.getOriginalFilename();
        String ext = ".jpg";
        if (originalName != null && originalName.contains(".")) {
            int dotIdx = originalName.lastIndexOf(".");
            if (dotIdx > 0) {
                ext = originalName.substring(dotIdx).toLowerCase();
            }
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("不支持的文件类型，仅支持: " + ALLOWED_EXTENSIONS);
        }
        try {
            String dir = System.getProperty("user.dir") + "/uploads/avatars/";
            Files.createDirectories(Paths.get(dir));
            String filename = UUID.randomUUID().toString() + ext;
            Path path = Paths.get(dir + filename);
            file.transferTo(path.toFile());
            String url = "/uploads/avatars/" + filename;

            User existing = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (existing != null) {
                existing.setAvatar(url);
                userMapper.updateById(existing);
            }
            return url;
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败: " + e.getMessage());
        }
    }
}
