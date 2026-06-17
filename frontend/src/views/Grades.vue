<template>
  <div>
    <h2 style="margin-bottom:16px">成绩管理</h2>

    <!-- 课程选择 -->
    <el-card style="margin-bottom:16px">
      <el-form :inline="true">
        <el-form-item label="选择课程">
          <el-select v-model="selectedCourseId" filterable placeholder="请选择课程" clearable @change="onCourseChange" style="width:300px">
            <el-option v-for="c in courses" :key="c.id" :label="c.name + ' (' + c.code + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据区域 -->
    <el-card v-if="selectedCourseId">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
        <span style="color:#64748b;font-size:13px">共 {{ total }} 条成绩记录</span>
        <el-button type="primary" @click="openAddDialog" v-if="auth.isAdmin || auth.isTeacher">录入成绩</el-button>
      </div>
      <el-table :data="grades" border stripe v-loading="loading">
        <el-table-column prop="studentId" label="学号" width="100" />
        <el-table-column label="姓名" width="120">
          <template #default="{ row }">{{ studentMap[row.studentId] || row.studentId }}</template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="140" />
        <el-table-column prop="score" label="分数" width="80" />
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev,pager,next"
        @current-change="fetchData"
        style="margin-top:12px;justify-content:center" />
    </el-card>

    <!-- 未选择课程 -->
    <el-card v-else>
      <el-empty description="请选择课程后查询" />
    </el-card>

    <!-- 录入成绩对话框 -->
    <el-dialog v-model="showAdd" title="录入成绩" width="480px" @closed="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生">
          <el-select v-model="form.studentId" filterable placeholder="请选择学生" style="width:100%">
            <el-option v-for="s in students" :key="s.id" :label="s.name + ' (' + s.username + ')'" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId" filterable placeholder="请选择课程" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.name + ' (' + c.code + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分数">
          <el-input-number v-model="form.score" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="form.semester" placeholder="如 2025-2026-1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { saveGrade } from '@/api'
import api from '@/api'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()

const courses = ref([])
const students = ref([])
const studentMap = reactive({})
const grades = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const selectedCourseId = ref('')
const showAdd = ref(false)
const form = ref({ studentId: '', courseId: '', score: 0, semester: '' })

onMounted(async () => {
  // 加载课程列表
  try {
    const r = await api.get('/courses?page=1&size=200')
    courses.value = r.data.records
  } catch (_) {}

  // 加载学生映射（用于姓名显示）
  try {
    const r = await api.get('/users/role/student')
    students.value = r.data
    r.data.forEach(s => { studentMap[s.id] = s.name })
  } catch (_) {}

  // 学生默认查看自己的成绩
  if (auth.isStudent && courses.value.length > 0) {
    // 学生没有 course filter 的预设，需要手动选课
  }
})

function onCourseChange() {
  page.value = 1
  if (selectedCourseId.value) {
    fetchData()
  } else {
    grades.value = []
    total.value = 0
  }
}

async function fetchData() {
  if (!selectedCourseId.value) return
  loading.value = true
  try {
    const params = { page: page.value, size: size.value, courseId: selectedCourseId.value }
    const res = await api.get('/grades', { params })
    grades.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '获取成绩失败')
  }
  loading.value = false
}

function openAddDialog() {
  form.value = { studentId: '', courseId: selectedCourseId.value || '', score: 0, semester: '' }
  showAdd.value = true
}

function resetForm() {
  form.value = { studentId: '', courseId: '', score: 0, semester: '' }
}

async function save() {
  try {
    await saveGrade(form.value)
    ElMessage.success('录入成功')
    showAdd.value = false
    fetchData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '保存失败')
  }
}
</script>

