<template>
  <div>
    <h2 style="margin-bottom:16px">课程管理</h2>
    <el-card>
      <el-table :data="courses" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="课程名称" width="180" />
        <el-table-column prop="credit" label="学分" width="60" />
        <el-table-column prop="maxStudents" label="容量" width="60" />
        <el-table-column prop="teacherName" label="任课老师" width="100" />
        <el-table-column prop="classroom" label="教室" width="130" />
        <el-table-column prop="schedule" label="时间" min-width="160" />
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev,pager,next"
        @current-change="fetchData"
        style="margin-top:12px;justify-content:center" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourses } from '@/api'
import { ElMessage } from 'element-plus'

const courses = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getCourses({ page: page.value, size: size.value })
    courses.value = res.data.records
    total.value = res.data.total
  } catch (e) { 
    ElMessage.error(e?.response?.data?.message || e?.message || '获取课程列表失败')
  }
  loading.value = false
}
</script>
