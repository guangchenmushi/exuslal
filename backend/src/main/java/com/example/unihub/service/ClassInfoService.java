package com.example.unihub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.ClassInfo;
import com.example.unihub.mapper.ClassInfoMapper;

@Service
public class ClassInfoService {

    private final ClassInfoMapper classInfoMapper;

    public ClassInfoService(ClassInfoMapper classInfoMapper) {
        this.classInfoMapper = classInfoMapper;
    }

    public Page<ClassInfo> list(int page, int size, String keyword) {
        Page<ClassInfo> p = new Page<>(page, size);
        LambdaQueryWrapper<ClassInfo> q = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            q.like(ClassInfo::getName, keyword).or().like(ClassInfo::getCode, keyword);
        }
        q.orderByAsc(ClassInfo::getId);
        return classInfoMapper.selectPage(p, q);
    }

    public ClassInfo getById(Integer id) {
        return classInfoMapper.selectById(id);
    }

    @Transactional
    public void add(ClassInfo classInfo) {
        classInfoMapper.insert(classInfo);
    }

    @Transactional
    public void update(ClassInfo classInfo) {
        classInfoMapper.updateById(classInfo);
    }

    @Transactional
    public void delete(Integer id) {
        classInfoMapper.deleteById(id);
    }
}
