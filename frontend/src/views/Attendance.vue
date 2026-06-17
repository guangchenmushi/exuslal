<template>
  <div>
    <div class="page-header"><h2>考勤管理</h2><p class="page-desc">按课程与日期查询学生出勤</p></div>
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="日期">
          <el-date-picker v-model="queryForm.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:180px" />
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="queryForm.courseId" placeholder="请选择课程" style="width:200px" clearable>
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-form-item v-if="auth.isTeacher || auth.isAdmin">
          <el-button type="success" @click="showAdd = true">记录考勤</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <div v-if="queryForm.date" class="session-info">
        {{ courseMap[queryForm.courseId] || '课程' }} · {{ queryForm.date }} · 考勤记录
      </div>
      <el-table :data="attendance" border stripe v-loading="loading" v-if="attendance.length > 0">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column label="考勤状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status === 'present' ? 'success' : row.status === 'late' ? 'warning' : row.status === 'absent' ? 'danger' : 'info'" effect="light" size="large">
              {{ row.status === 'present' ? '出勤' : row.status === 'late' ? '迟到' : row.status === 'absent' ? '缺勤' : row.status === 'leave' ? '请假' : row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else-if="!hasQueried" description="请先选择课程和日期，然后点击「查询」" />
      <el-empty v-else description="该课程在此日期暂无考勤记录" />
      <el-pagination
        v-if="total > 0"
        v-model:current-page="page" :page-size="size" :total="total"
        layout="prev,pager,next" @current-change="fetchData"
        style="margin-top:16px;justify-content:center" />
    </el-card>

    <el-dialog v-model="showAdd" title="记录考勤" width="480px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker v-model="addForm.date" type="date" placeholder="选择考勤日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="addForm.courseId" filterable placeholder="请选择课程" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生">
          <el-select v-model="addForm.studentId" filterable placeholder="请选择学生" style="width:100%">
            <el-option v-for="s in students" :key="s.id" :label="s.name + ' (' + s.username + ')'" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤状态">
          <el-select v-model="addForm.status" style="width:100%">
            <el-option label="出勤" value="present" />
            <el-option label="迟到" value="late" />
            <el-option label="缺勤" value="absent" />
            <el-option label="请假" value="leave" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确认记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAttendance, saveAttendance } from '@/api'
import { useAuthStore } from '@/stores/auth'
import api from '@/api'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const attendance = ref([])
const courses = ref([])
const courseMap = ref({})
const students = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showAdd = ref(false)
const hasQueried = ref(false)

const queryForm = ref({ courseId: null, date: null })
const addForm = ref({ studentId: '', courseId: '', date: '', status: 'present' })

onMounted(async () => {
  try {
    const r = await api.get('/courses?page=1&size=100')
    courses.value = r.data.records
    r.data.records.forEach(c => { courseMap.value[c.id] = c.name })
  } catch(e) {}
  try { const r = await api.get('/users/role/student'); students.value = r.data } catch(e) {}
})

async function fetchData() {
  if (!queryForm.value.courseId || !queryForm.value.date) {
    ElMessage.warning('请先选择课程和日期')
    attendance.value = []; total.value = 0; return
  }
  hasQueried.value = true
  loading.value = true
  try {
    const params = { page: page.value, size: size.value, courseId: queryForm.value.courseId, date: queryForm.value.date }
    const res = await getAttendance(params)
    const sorted = res.data.records.sort((a, b) => (a.studentId || 0) - (b.studentId || 0))
    attendance.value = sorted
    total.value = res.data.total
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '获取考勤记录失败')
  }
  loading.value = false
}

function resetQuery() {
  queryForm.value = { courseId: null, date: null }
  attendance.value = []
  total.value = 0
  page.value = 1
  hasQueried.value = false
}

async function handleSave() {
  try {
    await saveAttendance(addForm.value)
    ElMessage.success('记录成功')
    showAdd.value = false
    addForm.value = { studentId: '', courseId: '', date: '', status: 'present' }
    if (queryForm.value.courseId) fetchData()
  } catch(e) { ElMessage.error(e?.response?.data?.message || '记录失败') }
}
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
.filter-card { margin-bottom: 0; }
.filter-card :deep(.el-card__body) { padding: 16px 20px; }
.session-info { font-size: 15px; font-weight: 600; color: #0d9488; margin-bottom: 12px; padding: 8px 12px; background: #f0fdfa; border-radius: 6px; }
</style>