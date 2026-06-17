<template>
  <div>
    <div class="page-header"><h2>班级管理</h2><p class="page-desc">管理班级信息与学生分班</p></div>
    <el-card>
      <el-button v-if="auth.isAdmin" type="primary" @click="showAdd = true" style="margin-bottom:12px">添加班级</el-button>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="班级名称" min-width="160" />
        <el-table-column prop="code" label="班级编号" width="120" />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="major" label="专业" width="150" />
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev,pager,next" @current-change="fetchData" style="margin-top:12px;justify-content:center" />
    </el-card>

    <el-dialog v-model="showAdd" title="添加班级" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编号"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="年级"><el-input v-model="form.grade" placeholder="如 2024" /></el-form-item>
        <el-form-item label="专业"><el-input v-model="form.major" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/api'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const list = ref([]); const loading = ref(false)
const page = ref(1); const size = ref(20); const total = ref(0)
const showAdd = ref(false)
const form = ref({ name: '', code: '', grade: '', major: '' })
onMounted(() => fetchData())
async function fetchData() {
  loading.value = true
  try { const r = await api.get('/classes', { params: { page: page.value, size: size.value } }); list.value = r.data.records; total.value = r.data.total } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '获取班级列表失败') }
  loading.value = false
}
function resetForm() { form.value = { name: '', code: '', grade: '', major: '' } }
async function save() {
  try {
    await api.post('/classes', form.value)
    ElMessage.success('添加成功'); showAdd.value = false; resetForm(); fetchData()
  } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '添加失败') }
}
</script>
<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
</style>
