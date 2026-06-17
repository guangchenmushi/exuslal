<template>
  <div>
    <div class="page-header"><h2>考试管理</h2><p class="page-desc">考试安排与状态查询</p></div>
    <el-card>
      <el-button type="primary" @click="openAddDialog" style="margin-bottom:12px" v-if="auth.isAdmin">添加考试</el-button>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="title" label="考试名称" min-width="180" />
        <el-table-column label="考试时间" width="180">
          <template #default="{ row }">{{ (row.examDate || '').replace('T',' ') }}</template>
        </el-table-column>
        <el-table-column prop="location" label="考试地点" width="140" />
        <el-table-column label="考试状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="examStatus(row.examDate).type" size="small">{{ examStatus(row.examDate).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="参加人数" width="100" align="center">
          <template #default>
            <span style="color:#94a3b8">0</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev,pager,next"
        @current-change="fetchData"
        style="margin-top:12px;justify-content:center" />
    </el-card>

    <el-dialog v-model="showAdd" title="添加考试" width="500px" @closed="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId" filterable placeholder="请选择课程" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.name + ' (' + c.code + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试时间"><el-input v-model="form.examDate" placeholder="2026-06-30T09:00:00" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="总分"><el-input-number v-model="form.totalScore" :min="0" :max="200" /></el-form-item>
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
const courses = ref([])
const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const showAdd = ref(false)
const form = ref({ title: '', courseId: '', examDate: '', location: '', totalScore: 100 })

onMounted(() => {
  fetchData()
  api.get('/courses?page=1&size=200').then(r => { courses.value = r.data.records }).catch(() => {})
})

function examStatus(dateStr) {
  if (!dateStr) return { text: '未知', type: 'info' }
  const exam = new Date(dateStr)
  const now = new Date()
  const examDay = new Date(exam.getFullYear(), exam.getMonth(), exam.getDate())
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  if (examDay < today) return { text: '已结束', type: 'danger' }
  if (examDay > today) return { text: '未开始', type: 'success' }
  return { text: '进行中', type: 'warning' }
}

async function fetchData() {
  loading.value = true
  try {
    const r = await api.get('/exams', { params: { page: page.value, size: size.value } })
    list.value = r.data.records
    total.value = r.data.total
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '获取考试列表失败')
  }
  loading.value = false
}

function openAddDialog() {
  form.value = { title: '', courseId: '', examDate: '', location: '', totalScore: 100 }
  showAdd.value = true
}

function resetForm() {
  form.value = { title: '', courseId: '', examDate: '', location: '', totalScore: 100 }
}

async function save() {
  try {
    await api.post('/exams', form.value)
    ElMessage.success('添加成功')
    showAdd.value = false
    fetchData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '保存失败')
  }
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
</style>

