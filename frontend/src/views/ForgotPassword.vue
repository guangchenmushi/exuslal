<template>
  <div class="forgot-page">
    <div class="forgot-card ui-fade-in">
      <div class="forgot-card-accent"></div>
      <div class="forgot-logo">
        <h2 class="forgot-title">{{ step === 1 ? '忘记密码' : '重置密码' }}</h2>
        <p class="forgot-subtitle">{{ step === 1 ? '验证密保问题' : '设置新密码' }}</p>
      </div>

      <!-- 第一步：验证密保 -->
      <el-form v-if="step === 1" :model="form" @keyup.enter="verifySecurity">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item v-if="securityQuestion">
          <div class="security-q">{{ securityQuestion }}</div>
          <el-input v-model="form.answer" placeholder="请输入密保答案" size="large" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;height:44px;font-size:15px" @click="verifySecurity" :loading="loading" round>
            {{ loading ? '验证中...' : '验证密保' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 第二步：重置密码 -->
      <el-form v-if="step === 2" :model="form" @keyup.enter="resetPassword">
        <el-form-item>
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" size="large" show-password prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认新密码" size="large" show-password prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;height:44px;font-size:15px" @click="resetPassword" :loading="loading" round>
            {{ loading ? '重置中...' : '确认重置' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="forgot-footer">
        <span>想起密码了？</span>
        <router-link to="/login" class="forgot-link">返回登录</router-link>
      </div>
      <div class="forgot-error" v-if="error">{{ error }}</div>
      <div class="forgot-success" v-if="success">{{ success }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const step = ref(1)
const loading = ref(false)
const error = ref('')
const success = ref('')
const securityQuestion = ref('')
const form = reactive({ username: '', answer: '', newPassword: '', confirmPassword: '' })

async function verifySecurity() {
  if (!form.username) { error.value = '请输入用户名'; return }
  loading.value = true; error.value = ''; securityQuestion.value = ''
  try {
    // 先获取用户信息，看看密保问题
    const r = await api.get('/users/role/student')
    const users = r.data
    const user = users.find(u => u.username === form.username)
    if (!user || !user.securityQuestion) {
      error.value = '该用户未设置密保问题，请联系管理员'
      return
    }
    if (!form.answer) {
      securityQuestion.value = user.securityQuestion
      return
    }
    // 验证答案
    await api.post('/auth/verify-security', { username: form.username, answer: form.answer })
    securityQuestion.value = ''
    step.value = 2
  } catch (e) {
    error.value = e?.response?.data?.message || e?.message || '验证失败'
  } finally { loading.value = false }
}

async function resetPassword() {
  if (!form.newPassword || form.newPassword.length < 6) { error.value = '密码至少6位'; return }
  if (form.newPassword !== form.confirmPassword) { error.value = '两次密码不一致'; return }
  loading.value = true; error.value = ''
  try {
    await api.post('/auth/reset-password', { username: form.username, newPassword: form.newPassword })
    success.value = '密码重置成功！'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e) {
    error.value = e?.response?.data?.message || e?.message || '重置失败'
  } finally { loading.value = false }
}
</script>

<style scoped>
.security-q {
  width: 100%; padding: 8px 12px; margin-bottom: 4px;
  background: rgba(13,148,136,0.08); border-radius: 8px;
  color: #0d9488; font-size: 14px; text-align: center;
}
.forgot-success {
  margin-top: 12px; padding: 8px 16px; background: rgba(16,185,129,0.1);
  color: #10b981; border-radius: 8px; font-size: 13px; text-align: center;
}
</style>

<style scoped>
.forgot-page {
  height: 100vh; display: flex; align-items: center; justify-content: center;
  background: url('/img/14.jpg') center/cover fixed; position: relative;
}
.forgot-page::before {
  content: ''; position: absolute; inset: 0;
  background: rgba(15, 23, 42, 0.35); z-index: 0;
}
.forgot-card {
  position: relative; z-index: 1; width: 420px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.60);
  border-radius: 20px; padding: 40px 36px 32px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}
.forgot-card-accent {
  position: absolute; top: 0; left: 0; right: 0; height: 4px;
  background: linear-gradient(90deg, #4f46e5, #818cf8, #6366f1);
}
.forgot-logo { text-align: center; margin-bottom: 28px; }
.forgot-title { font-size: 24px; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
.forgot-subtitle { font-size: 13px; color: #64748b; }
.forgot-footer { text-align: center; margin-top: 16px; font-size: 13px; color: #64748b; }
.forgot-link { color: #4f46e5; text-decoration: none; font-weight: 500; }
.forgot-link:hover { text-decoration: underline; }
.forgot-error { color: #ef4444; text-align: center; margin-top: 12px; font-size: 13px; background: rgba(239,68,68,0.08); padding: 8px 12px; border-radius: 8px; }
.ui-fade-in { animation: fadeIn 0.5s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }
</style>
