<template>
  <div>
    <h2 style="margin-bottom:16px">课程管理</h2>
    <el-card>
      <el-button type="primary" @click="showAdd = true" v-if="auth.isAdmin" style="margin-bottom:12px">添加课程</el-button>
      <el-table :data="courses" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="课程编号" width="120" />
        <el-table-column prop="name" label="课程名称" width="150" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="maxStudents" label="容量" width="60" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column prop="schedule" label="时间" min-width="160" />
        <el-table-column label="操作" width="180" fixed="right" v-if="auth.isAdmin">
          <template #default="{ row }">
            <el-button size="small" @click="edit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
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

    <el-dialog v-model="showAdd" :title="editing ? '编辑课程' : '添加课程'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程编号"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="课程名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="form.credit" :min="1" :max="10" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.maxStudents" :min="10" :max="200" /></el-form-item>
        <el-form-item label="教室"><el-input v-model="form.classroom" /></el-form-item>
        <el-form-item label="上课时间"><el-input v-model="form.schedule" placeholder="如 周一 1-2节" /></el-form-item>
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
import { getCourses, addCourse, updateCourse, deleteCourse } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const courses = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const showAdd = ref(false)
const editing = ref(false)
const form = ref({ code: '', name: '', credit: 3, maxStudents: 50, classroom: '', schedule: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getCourses({ page: page.value, size: size.value })
    courses.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* ignore */ }
  loading.value = false
}

function edit(row) {
  editing.value = true
  form.value = { ...row }
  showAdd.value = true
}

function resetForm() {
  form.value = { code: '', name: '', credit: 3, maxStudents: 50, classroom: '', schedule: '' }
  editing.value = false
}

async function save() {
  if (editing.value) {
    await updateCourse(form.value)
  } else {
    await addCourse(form.value)
  }
  showAdd.value = false
  resetForm()
  fetchData()
}

async function remove(id) {
  await deleteCourse(id)
  fetchData()
}
</script>
