<template>
  <div class="login-page">
    <div class="login-card ui-fade-in">
      <div class="login-card-accent"></div>
      <div class="login-logo">
        <h2 class="login-title">UniHub</h2>
        <p class="login-subtitle">校园管理系统</p>
      </div>
      <el-form :model="form" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="验证码" size="large" style="flex:1" maxlength="4" />
            <div class="captcha-box" @click="refreshCaptcha">
              <span class="captcha-text" v-html="captchaExpr"></span>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;height:44px;font-size:15px" @click="handleLogin" :loading="loading" round>
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <router-link to="/register" class="login-link">还没有账号？立即注册</router-link>
        <router-link to="/forgot-password" class="login-link">忘记密码？</router-link>
      </div>
      <div class="login-error" v-if="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const error = ref('')
const captchaExpr = ref('')
const captchaResult = ref(0)
const form = reactive({ username: '', password: '', captcha: '' })

function refreshCaptcha() {
  const a = Math.floor(Math.random() * 10) + 1
  const b = Math.floor(Math.random() * 10) + 1
  const ops = ['+', 'x']
  const op = ops[Math.floor(Math.random() * ops.length)]
  captchaExpr.value = a + ' ' + op + ' ' + b + ' = ?'
  captchaResult.value = op === '+' ? a + b : a * b
}

onMounted(() => refreshCaptcha())

async function handleLogin() {
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码'
    return
  }
  if (parseInt(form.captcha) !== captchaResult.value) {
    error.value = '验证码错误'
    refreshCaptcha()
    return
  }
  loading.value = true
  error.value = ''
  try {
    await auth.login(form)
    router.push('/dashboard')
  } catch (e) {
    error.value = e?.message || '登录失败，请检查用户名和密码'
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('/img/13.jpg') center/cover fixed;
  position: relative;
}
.login-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  z-index: 0;
}
.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  background: rgba(255, 255, 255, 0.60);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.60);
  border-radius: 20px;
  padding: 40px 36px 32px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}
.login-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 32px 80px rgba(15, 23, 42, 0.22);
}
.login-card-accent {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #0d9488, #14b8a6, #0891b2);
}
.login-logo { text-align: center; margin-bottom: 32px; }
.login-title {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
  margin-bottom: 4px;
}
.login-subtitle {
  font-size: 13px;
  color: #64748b;
}
.captcha-row { display: flex; gap: 12px; align-items: center; width: 100%; }
.captcha-box {
  width: 120px; height: 40px;
  background: linear-gradient(135deg, #0d9488, #0891b2);
  border-radius: 8px; display: flex; align-items: center;
  justify-content: center; cursor: pointer; user-select: none;
  flex-shrink: 0;
}
.captcha-text { color: #fff; font-size: 18px; font-weight: 700; letter-spacing: 3px; }
.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  font-size: 13px;
}
.login-link {
  color: #64748b;
  text-decoration: none;
  transition: color 0.2s;
}
.login-link:hover { color: #4f46e5; text-decoration: underline; }
.login-error {
  color: #ef4444;
  text-align: center;
  margin-top: 12px;
  font-size: 13px;
  background: rgba(239,68,68,0.08);
  padding: 8px 12px;
  border-radius: 8px;
}
.ui-fade-in {
  animation: fadeIn 0.5s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
