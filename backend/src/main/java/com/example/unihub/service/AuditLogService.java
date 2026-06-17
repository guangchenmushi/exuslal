package com.example.unihub.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.unihub.entity.AuditLog;
import com.example.unihub.mapper.AuditLogMapper;

@Service
public class AuditLogService {
    private final AuditLogMapper auditLogMapper;
    public AuditLogService(AuditLogMapper auditLogMapper) { this.auditLogMapper = auditLogMapper; }

    public Page<AuditLog> list(int page, int size) {
        return auditLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<AuditLog>().orderByDesc(AuditLog::getCreateTime));
    }
}
