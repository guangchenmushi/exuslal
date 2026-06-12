<template>
  <div>
    <h2 style="margin-bottom:16px">考勤管理</h2>
    <el-card>
      <el-button v-if="auth.isTeacher || auth.isAdmin" type="primary" @click="showAdd = true" style="margin-bottom:12px">记录考勤</el-button>
      <el-table :data="attendance" border stripe v-loading="loading">
        <el-table-column prop="studentId" label="学生ID" width="80" />
        <el-table-column prop="courseId" label="课程ID" width="80" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column prop="date" label="日期" width="170" />
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev,pager,next"
        @current-change="fetchData"
        style="margin-top:12px;justify-content:center" />
    </el-card>

    <el-dialog v-model="showAdd" title="记录考勤" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生ID"><el-input v-model="form.studentId" /></el-form-item>
        <el-form-item label="课程ID"><el-input v-model="form.courseId" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="出勤" value="present" />
            <el-option label="缺勤" value="absent" />
            <el-option label="迟到" value="late" />
            <el-option label="请假" value="leave" />
          </el-select>
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
import { ref, onMounted } from 'vue'
import { getAttendance, saveAttendance } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const attendance = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const showAdd = ref(false)
const form = ref({ studentId: '', courseId: '', status: 'present' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getAttendance({ page: page.value, size: size.value })
    attendance.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function save() {
  await saveAttendance(form.value)
  showAdd.value = false
  form.value = { studentId: '', courseId: '', status: 'present' }
  fetchData()
}
</script>
