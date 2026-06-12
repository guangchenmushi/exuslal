package com.example.unihub.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.Announcement;
import com.example.unihub.mapper.AnnouncementMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final StringRedisTemplate redisTemplate;

    public AnnouncementService(AnnouncementMapper announcementMapper,
                                StringRedisTemplate redisTemplate) {
        this.announcementMapper = announcementMapper;
        this.redisTemplate = redisTemplate;
    }

    public List<Announcement> getActive(String role) {
        return announcementMapper.selectList(
                new LambdaQueryWrapper<Announcement>()
                        .in(Announcement::getTargetRole, role, "all")
                        .orderByDesc(Announcement::getPublishTime));
    }

    public Page<Announcement> list(int page, int size) {
        return announcementMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Announcement>()
                        .orderByDesc(Announcement::getPublishTime));
    }

    public void add(Announcement announcement) {
        announcementMapper.insert(announcement);
        clearCache();
    }

    public void update(Announcement announcement) {
        announcementMapper.updateById(announcement);
        clearCache();
    }

    public void delete(Integer id) {
        announcementMapper.deleteById(id);
        clearCache();
    }

    private void clearCache() {
        redisTemplate.delete("announcements:admin");
        redisTemplate.delete("announcements:teacher");
        redisTemplate.delete("announcements:student");
    }
}
