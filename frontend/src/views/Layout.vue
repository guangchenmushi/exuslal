<template>
  <el-container style="height:100vh">
    <el-aside width="220px" class="aside">
      <div class="logo">UniHub</div>
      <el-menu :default-active="route.path" router background-color="#1e293b" text-color="#94a3b8" active-text-color="#fff">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/students" v-if="auth.isAdmin">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="/courses">
          <el-icon><Notebook /></el-icon>
          <span>课程管理</span>
        </el-menu-item>
        <el-menu-item index="/selections" v-if="auth.isStudent">
          <el-icon><Select /></el-icon>
          <span>选课中心</span>
        </el-menu-item>
        <el-menu-item index="/grades">
          <el-icon><Document /></el-icon>
          <span>成绩管理</span>
        </el-menu-item>
        <el-menu-item index="/attendance">
          <el-icon><Check /></el-icon>
          <span>考勤管理</span>
        </el-menu-item>
        <el-menu-item index="/announcements">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><Setting /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="welcome">欢迎，{{ auth.name || auth.username }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { DataAnalysis, User, Notebook, Select, Document, Check, Bell, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.aside {
  background: #1e293b;
  overflow-y: auto;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  letter-spacing: 2px;
}
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  gap: 16px;
}
.welcome {
  color: #475569;
  font-size: 14px;
}
.main {
  background: #f1f5f9;
}
</style>
