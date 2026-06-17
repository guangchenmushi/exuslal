<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="page-desc">个人信息与账户设置</p>
    </div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-head">
            <div class="profile-avatar" @click="triggerUpload">
              <img :src="profile.avatar ? '/uploads/avatars/' + profile.avatar?.split('/').pop() : '/img/default-avatar.png'" alt="avatar" />
              <div class="avatar-overlay">点击更换</div>
            </div>
            <div class="profile-meta">
              <div class="profile-name">{{ auth.name || auth.username }}</div>
              <div class="profile-role">{{ roleMap[auth.role] || auth.role }}</div>
            </div>
          </div>
          <input type="file" ref="avatarInput" accept="image/*" style="display:none" @change="uploadAvatar" />
          <el-descriptions :column="1" direction="horizontal" border>
            <el-descriptions-item label="用户名">{{ auth.username }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ profile.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ profile.gender || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ profile.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ profile.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="爱好">{{ profile.hobby || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ roleMap[auth.role] || auth.role }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="profile-card">
          <template #header><span class="card-title">编辑资料</span></template>
          <el-form :model="editForm" label-width="80px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名"><el-input v-model="editForm.name" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-select v-model="editForm.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="电话"><el-input v-model="editForm.phone" /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="爱好"><el-input v-model="editForm.hobby" /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
          <div v-if="saveMsg" :style="{ color: saveMsg.includes('成功') ? '#10b981' : '#ef4444', marginTop: '8px', fontSize: '13px' }">{{ saveMsg }}</div>
        </el-card>

        <el-card class="profile-card" style="margin-top:16px">
          <template #header><span class="card-title">修改密码</span></template>
          <el-form :model="pwdForm" label-width="80px">
            <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item label="确认密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="pwdSaving">修改密码</el-button>
            </el-form-item>
          </el-form>
          <div v-if="pwdMsg" :style="{ color: pwdMsg.includes('成功') ? '#10b981' : '#ef4444', marginTop: '8px', fontSize: '13px' }">{{ pwdMsg }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/api'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const roleMap = { admin: '管理员', teacher: '教师', student: '学生' }
const profile = ref({})
const editForm = ref({ name: '', gender: '', phone: '', email: '', hobby: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const saving = ref(false)
const pwdSaving = ref(false)
const saveMsg = ref('')
const pwdMsg = ref('')
const avatarInput = ref(null)

onMounted(async () => {
  try {
    const r = await api.get('/users/role/' + auth.role)
    const users = r.data
    const me = users.find(u => u.username === auth.username)
    if (me) {
      profile.value = me
      editForm.value = { name: me.name || '', gender: me.gender || '', phone: me.phone || '', email: me.email || '', hobby: me.hobby || '' }
    }
  } catch(e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '获取个人信息失败')
  }
})

async function saveProfile() {
  saving.value = true
  saveMsg.value = ''
  try {
    await api.put('/users/profile', { ...profile.value, ...editForm.value })
    saveMsg.value = '保存成功'
    if (editForm.value.name) auth.name = editForm.value.name
    Object.assign(profile.value, editForm.value)
  } catch(e) {
    saveMsg.value = '保存失败：' + (e?.message || '未知错误')
  }
  saving.value = false
  setTimeout(() => saveMsg.value = '', 3000)
}

function triggerUpload() { avatarInput.value?.click() }

async function uploadAvatar(e) {
  const file = e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await api.post('/users/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    profile.value.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch(e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '上传失败')
  }
}

async function changePassword() {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) { pwdMsg.value = '请填写完整'; return }
  if (pwdForm.value.newPassword.length < 6) { pwdMsg.value = '新密码至少6位'; return }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) { pwdMsg.value = '两次密码不一致'; return }
  pwdSaving.value = true
  pwdMsg.value = ''
  try {
    await api.post('/auth/change-password', {
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    pwdMsg.value = '密码修改成功'
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch(e) {
    pwdMsg.value = '修改失败：' + (e?.response?.data?.message || e?.message || '未知错误')
  }
  pwdSaving.value = false
  setTimeout(() => pwdMsg.value = '', 3000)
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
.profile-card { border: none !important; height: 100%; }
.profile-head {
  display: flex; align-items: center; gap: 20px;
  padding-bottom: 20px; margin-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}
.profile-avatar {
  width: 72px; height: 72px; border-radius: 50%; overflow: hidden;
  border: 3px solid rgba(13, 148, 136, 0.2); flex-shrink: 0;
  cursor: pointer; position: relative;
}
.profile-avatar img { width: 100%; height: 100%; object-fit: cover; }
.avatar-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(0,0,0,0.5); color: #fff; font-size: 11px;
  text-align: center; padding: 4px 0; opacity: 0;
  transition: opacity 0.2s;
}
.profile-avatar:hover .avatar-overlay { opacity: 1; }
.profile-name { font-size: 20px; font-weight: 700; color: #0f172a; }
.profile-role {
  font-size: 13px; margin-top: 4px; display: inline-block;
  background: rgba(13, 148, 136, 0.08); color: #0d9488;
  padding: 2px 12px; border-radius: 999px; font-weight: 500;
}
.card-title { font-size: 15px; font-weight: 600; color: #0f172a; }
</style>
