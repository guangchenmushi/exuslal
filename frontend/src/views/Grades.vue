<template>
  <div>
    <h2 style="margin-bottom:16px">成绩管理</h2>
    <el-card>
      <template v-if="auth.isStudent">
        <el-table :data="grades" border stripe>
          <el-table-column prop="courseId" label="课程ID" width="80" />
          <el-table-column prop="score" label="成绩" width="100" />
          <el-table-column prop="semester" label="学期" width="120" />
        </el-table>
      </template>
      <template v-else>
        <el-table :data="grades" border stripe v-loading="loading">
          <el-table-column prop="studentId" label="学生ID" width="80" />
          <el-table-column prop="courseId" label="课程ID" width="80" />
          <el-table-column prop="score" label="成绩" width="80" />
          <el-table-column prop="semester" label="学期" width="120" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="edit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <el-pagination
        v-if="!auth.isStudent"
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev,pager,next"
        @current-change="fetchData"
        style="margin-top:12px;justify-content:center" />
    </el-card>

    <el-dialog v-model="showEdit" title="编辑成绩" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生ID"><el-input v-model="form.studentId" /></el-form-item>
        <el-form-item label="课程ID"><el-input v-model="form.courseId" /></el-form-item>
        <el-form-item label="成绩"><el-input-number v-model="form.score" :min="0" :max="100" /></el-form-item>
        <el-form-item label="学期"><el-input v-model="form.semester" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getGrades, getMyGrades, saveGrade, updateGrade, deleteGrade } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const grades = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const showEdit = ref(false)
const editing = ref(false)
const form = ref({ studentId: '', courseId: '', score: 0, semester: '' })

onMounted(() => {
  if (auth.isStudent) {
    getMyGrades().then(res => { grades.value = res.data }).catch(() => {})
  } else {
    fetchData()
  }
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getGrades({ page: page.value, size: size.value })
    grades.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* ignore */ }
  loading.value = false
}

function edit(row) {
  editing.value = true
  form.value = { ...row }
  showEdit.value = true
}

async function save() {
  if (editing.value) {
    await updateGrade(form.value)
  } else {
    await saveGrade(form.value)
  }
  showEdit.value = false
  fetchData()
}

async function remove(id) {
  await deleteGrade(id)
  fetchData()
}
</script>
