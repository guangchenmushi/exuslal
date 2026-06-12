<template>
  <div>
    <h2 style="margin-bottom:16px">个人中心</h2>
    <el-card style="max-width:600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ auth.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ auth.name }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleMap[auth.role] || auth.role }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const roleMap = { admin: '管理员', teacher: '教师', student: '学生' }
</script>
