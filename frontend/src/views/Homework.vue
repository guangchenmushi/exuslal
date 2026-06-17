<template>
  <div>
    <div class="page-header"><h2>作业管理</h2><p class="page-desc">发布与查看课程作业</p></div>

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

    <!-- 作业列表 -->
    <el-card v-if="selectedCourseId">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
        <span style="color:#64748b;font-size:13px">共 {{ total }} 项作业</span>
        <el-button type="primary" @click="openAddDialog" v-if="auth.isAdmin || auth.isTeacher">发布作业</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading" @row-click="showDetail" :row-style="{ cursor: 'pointer' }">
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="截止时间" width="180">
          <template #default="{ row }">{{ (row.deadline || '').replace('T',' ') }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="deadlineStatus(row.deadline).type" size="small">{{ deadlineStatus(row.deadline).text }}</el-tag>
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

    <!-- 未选择课程 -->
    <el-card v-else>
      <el-empty description="请选择课程后查看作业" />
    </el-card>

    <!-- 作业详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="作业详情" width="560px">
      <template v-if="detailRow">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="标题">{{ detailRow.title }}</el-descriptions-item>
          <el-descriptions-item label="内容">{{ detailRow.content }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ (detailRow.deadline || '').replace('T',' ') }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="deadlineStatus(detailRow.deadline).type" size="small">{{ deadlineStatus(detailRow.deadline).text }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <el-divider content-position="left">提交情况</el-divider>
        <el-empty description="暂无提交数据" :image-size="60" />
      </template>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 发布作业对话框 -->
    <el-dialog v-model="showAdd" title="发布作业" width="500px" @closed="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId" filterable placeholder="请选择课程" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.name + ' (' + c.code + ')'" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="截止时间"><el-input v-model="form.deadline" placeholder="2026-06-30T23:59:00" /></el-form-item>
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
const selectedCourseId = ref('')
const showAdd = ref(false)
const showDetailDialog = ref(false)
const detailRow = ref(null)
const form = ref({ title: '', courseId: '', content: '', deadline: '' })

onMounted(() => {
  api.get('/courses?page=1&size=200').then(r => { courses.value = r.data.records }).catch(() => {})
})

function deadlineStatus(dateStr) {
  if (!dateStr) return { text: '未知', type: 'info' }
  const dl = new Date(dateStr)
  const now = new Date()
  if (dl < now) return { text: '已截止', type: 'danger' }
  return { text: '进行中', type: 'success' }
}

function onCourseChange() {
  page.value = 1
  if (selectedCourseId.value) {
    fetchData()
  } else {
    list.value = []
    total.value = 0
  }
}

async function fetchData() {
  if (!selectedCourseId.value) return
  loading.value = true
  try {
    const r = await api.get('/homework', { params: { page: page.value, size: size.value, courseId: selectedCourseId.value } })
    list.value = r.data.records
    total.value = r.data.total
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || '获取作业列表失败')
  }
  loading.value = false
}

function showDetail(row) {
  detailRow.value = row
  showDetailDialog.value = true
}

function openAddDialog() {
  form.value = { title: '', courseId: selectedCourseId.value || '', content: '', deadline: '' }
  showAdd.value = true
}

function resetForm() {
  form.value = { title: '', courseId: '', content: '', deadline: '' }
}

async function save() {
  try {
    await api.post('/homework', form.value)
    ElMessage.success('发布成功')
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

