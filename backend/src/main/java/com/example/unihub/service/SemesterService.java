package com.example.unihub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Semester;
import com.example.unihub.mapper.SemesterMapper;

@Service
public class SemesterService {

    private final SemesterMapper semesterMapper;

    public SemesterService(SemesterMapper semesterMapper) {
        this.semesterMapper = semesterMapper;
    }

    public Page<Semester> list(int page, int size) {
        return semesterMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Semester>().orderByDesc(Semester::getId));
    }

    public Semester getCurrent() {
        return semesterMapper.selectOne(
                new LambdaQueryWrapper<Semester>().eq(Semester::getIsCurrent, true));
    }

    @Transactional
    public void add(Semester semester) {
        semesterMapper.insert(semester);
    }

    @Transactional
    public void update(Semester semester) {
        semesterMapper.updateById(semester);
    }

    @Transactional
    public void delete(Integer id) {
        semesterMapper.deleteById(id);
    }
}
