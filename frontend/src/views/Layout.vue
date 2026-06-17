<template>
  <el-container style="height:100vh">
    <el-aside :width="sidebarWidth" class="aside">
      <div class="sidebar-header">
        <div class="logo">UniHub</div>
      </div>
      <div class="user-profile">
        <div class="avatar-wrap">
          <img src="/img/default-avatar.png" class="avatar" alt="avatar" />
        </div>
        <div class="user-name">{{ auth.name || auth.username }}</div>
        <div class="user-role">{{ roleMap[auth.role] || auth.role }}</div>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="transparent"
        text-color="#94a3b8"
        active-text-color="#fff"
        class="sidebar-menu"
      >
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
        <el-menu-item index="/homework">
          <el-icon><Edit /></el-icon>
          <span>作业管理</span>
        </el-menu-item>
        <el-menu-item index="/exams">
          <el-icon><Coin /></el-icon>
          <span>考试管理</span>
        </el-menu-item>
        <el-menu-item index="/announcements">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/classes" v-if="auth.isAdmin">
          <el-icon><OfficeBuilding /></el-icon>
          <span>班级管理</span>
        </el-menu-item>
        <el-menu-item index="/semesters" v-if="auth.isAdmin">
          <el-icon><Timer /></el-icon>
          <span>学期管理</span>
        </el-menu-item>
        <el-menu-item index="/audit-logs" v-if="auth.isAdmin">
          <el-icon><List /></el-icon>
          <span>审计日志</span>
        </el-menu-item>
        <el-menu-item index="/login-logs" v-if="auth.isAdmin">
          <el-icon><Clock /></el-icon>
          <span>登录日志</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><Setting /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <el-button text style="color:#94a3b8;width:100%;justify-content:flex-start;padding-left:16px" @click="themeStore.toggle()">
          <el-icon><Moon /></el-icon>
          <span style="margin-left:8px">{{ themeStore.isDark ? '日间模式' : '夜间模式' }}</span>
        </el-button>
        <el-button text style="color:#94a3b8;width:100%;justify-content:flex-start;padding-left:16px" @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <span style="margin-left:8px">退出登录</span>
        </el-button>
      </div>
    </el-aside>
    <el-container>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import {
  DataAnalysis, User, Notebook, Select, Document,
  Check, Bell, Edit, Coin, List, Clock, Moon, Setting, SwitchButton,
  OfficeBuilding, Timer
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const themeStore = useThemeStore()
const roleMap = { admin: '管理员', teacher: '教师', student: '学生' }
const sidebarWidth = '220px'

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.aside {
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  user-select: none;
}
.sidebar-header {
  padding: 20px 0 16px;
  text-align: center;
}
.logo {
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #0d9488, #14b8a6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.user-profile {
  text-align: center;
  padding: 0 16px 20px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
  margin-bottom: 8px;
}
.avatar-wrap {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  margin: 0 auto 10px;
  border: 2px solid rgba(13, 148, 136, 0.4);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.1);
}
.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.user-name {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
}
.user-role {
  color: #64748b;
  font-size: 12px;
  margin-top: 2px;
}
.sidebar-menu {
  flex: 1;
  border: none !important;
  padding: 4px 8px;
}
.sidebar-menu .el-menu-item {
  border-radius: 8px;
  margin: 2px 0;
  height: 44px;
  line-height: 44px;
}
.sidebar-menu .el-menu-item:hover {
  background: rgba(255,255,255,0.06) !important;
}
.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(13, 148, 136, 0.35), rgba(13, 148, 136, 0.15)) !important;
  border-right: none !important;
  color: #fff !important;
}
.sidebar-footer {
  padding: 12px 8px;
  border-top: 1px solid rgba(148, 163, 184, 0.12);
}
.main {
  background: #f0f4f8;
  padding: 24px;
  overflow-y: auto;
}
</style>
