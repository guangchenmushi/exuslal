<template>
  <div class="register-page">
    <div class="register-card ui-fade-in">
      <div class="register-card-accent"></div>
      <div class="register-logo">
        <h2 class="register-title">注册账号</h2>
        <p class="register-subtitle">创建 UniHub 校园管理系统账号</p>
      </div>
      <el-form :model="form" @keyup.enter="handleRegister">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item><el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item><el-input v-model="form.stuId" placeholder="学号" size="large" prefix-icon="Edit" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item><el-input v-model="form.name" placeholder="姓名" size="large" prefix-icon="Edit" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password prefix-icon="Lock" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item><el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password prefix-icon="Lock" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item>
              <el-select v-model="form.gender" placeholder="性别" size="large" style="width:100%">
                <el-option label="男" value="男" /><el-option label="女" value="女" /><el-option label="保密" value="保密" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item><el-input v-model="form.phone" placeholder="电话" size="large" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item><el-input v-model="form.email" placeholder="邮箱" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.hobby" placeholder="爱好" size="large" /></el-form-item>
        <el-form-item>
          <el-select v-model="form.securityQuestion" placeholder="选择密保问题" size="large" style="width:100%">
            <el-option label="你的生日是？" value="你的生日是？" />
            <el-option label="你的小学名字是？" value="你的小学名字是？" />
            <el-option label="你最喜欢的科目是？" value="你最喜欢的科目是？" />
            <el-option label="你的家乡是？" value="你的家乡是？" />
            <el-option label="你的宠物名字是？" value="你的宠物名字是？" />
          </el-select>
        </el-form-item>
        <el-form-item><el-input v-model="form.securityAnswer" placeholder="密保答案" size="large" prefix-icon="Lock" /></el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%;height:44px;font-size:15px" @click="handleRegister" :loading="loading" round>
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="register-link">立即登录</router-link>
      </div>
      <div class="register-error" v-if="error">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const form = reactive({
  username: '', stuId: '', name: '', password: '', confirmPassword: '',
  gender: '', phone: '', email: '', hobby: '',
  securityQuestion: '', securityAnswer: ''
})

async function handleRegister() {
  if (!form.username || !form.password || !form.name) {
    error.value = '请填写完整信息'
    return
  }
  if (form.password !== form.confirmPassword) {
    error.value = '两次密码不一致'
    return
  }
  if (form.password.length < 6) {
    error.value = '密码至少6位'
    return
  }
  loading.value = true
  error.value = ''
  try {
    await api.post('/auth/register', {
      username: form.username, stuId: form.stuId, password: form.password,
      name: form.name, gender: form.gender, phone: form.phone,
      email: form.email, hobby: form.hobby,
      securityQuestion: form.securityQuestion, securityAnswer: form.securityAnswer
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    error.value = e?.response?.data?.message || e?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  height: 100vh; display: flex; align-items: center; justify-content: center;
  background: url('/img/12.jpg') center/cover fixed; position: relative;
}
.register-page::before {
  content: ''; position: absolute; inset: 0;
  background: rgba(15, 23, 42, 0.35); z-index: 0;
}
.register-card {
  position: relative; z-index: 1; width: 420px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.60);
  border-radius: 20px; padding: 40px 36px 32px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}
.register-card-accent {
  position: absolute; top: 0; left: 0; right: 0; height: 4px;
  background: linear-gradient(90deg, #4f46e5, #818cf8, #6366f1);
}
.register-logo { text-align: center; margin-bottom: 28px; }
.register-title { font-size: 24px; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
.register-subtitle { font-size: 13px; color: #64748b; }
.register-footer { text-align: center; margin-top: 16px; font-size: 13px; color: #64748b; }
.register-link { color: #4f46e5; text-decoration: none; font-weight: 500; }
.register-link:hover { text-decoration: underline; }
.register-error { color: #ef4444; text-align: center; margin-top: 12px; font-size: 13px; background: rgba(239,68,68,0.08); padding: 8px 12px; border-radius: 8px; }
.ui-fade-in { animation: fadeIn 0.5s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }
</style>
