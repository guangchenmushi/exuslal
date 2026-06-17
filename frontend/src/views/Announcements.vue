<template>
  <div>
    <h2 style="margin-bottom:16px">公告管理</h2>
    <el-card>
      <el-button v-if="auth.isAdmin" type="primary" @click="showAdd = true" style="margin-bottom:12px">发布公告</el-button>
      <el-table :data="announcements" border stripe v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="targetRole" label="目标角色" width="100" />
        <el-table-column label="发布时间" width="170"><template #default="{row}">{{ (row.publishTime || '').replace('T',' ') }}</template></el-table-column>
        <el-table-column label="操作" width="120" fixed="right" v-if="auth.isAdmin">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAdd" title="发布公告" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="目标角色">
          <el-select v-model="form.targetRole">
            <el-option label="全部" value="all" />
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="save">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAnnouncements, addAnnouncement, deleteAnnouncement } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const announcements = ref([])
const loading = ref(false)
const showAdd = ref(false)
const form = ref({ title: '', content: '', targetRole: 'all' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getAnnouncements({ page: 1, size: 100 })
    announcements.value = res.data.records
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '获取公告列表失败')
  }
  loading.value = false
}

async function save() {
  try {
    await addAnnouncement(form.value)
    ElMessage.success('发布成功')
    showAdd.value = false
    form.value = { title: '', content: '', targetRole: 'all' }
    fetchData()
  } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '发布失败') }
}

async function remove(id) {
  try {
    await deleteAnnouncement(id)
    fetchData()
  } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '删除失败') }
}
</script>
