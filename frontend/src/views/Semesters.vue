<template>
  <div>
    <div class="page-header"><h2>学期管理</h2><p class="page-desc">学期设置与切换</p></div>
    <el-card>
      <el-button v-if="auth.isAdmin" type="primary" @click="showAdd = true" style="margin-bottom:12px">添加学期</el-button>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="学期名称" min-width="200" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column label="开始日期" width="120"><template #default="{row}">{{ row.startDate }}</template></el-table-column>
        <el-table-column label="结束日期" width="120"><template #default="{row}">{{ row.endDate }}</template></el-table-column>
        <el-table-column label="当前学期" width="100">
          <template #default="{row}"><el-tag :type="row.isCurrent ? 'success' : 'info'">{{ row.isCurrent ? '是' : '否' }}</el-tag></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev,pager,next" @current-change="fetchData" style="margin-top:12px;justify-content:center" />
    </el-card>

    <el-dialog v-model="showAdd" title="添加学期" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学期名称"><el-input v-model="form.name" placeholder="2024-2025学年第一学期" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" placeholder="2024-1" /></el-form-item>
        <el-form-item label="开始日期"><el-input v-model="form.startDate" placeholder="2024-09-01" /></el-form-item>
        <el-form-item label="结束日期"><el-input v-model="form.endDate" placeholder="2025-01-15" /></el-form-item>
        <el-form-item label="设为当前">
          <el-switch v-model="form.isCurrent" />
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
import { useAuthStore } from '@/stores/auth'
import api from '@/api'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const list = ref([]); const loading = ref(false)
const page = ref(1); const size = ref(20); const total = ref(0)
const showAdd = ref(false)
const form = ref({ name: '', code: '', startDate: '', endDate: '', isCurrent: false })
onMounted(() => fetchData())
async function fetchData() {
  loading.value = true
  try { const r = await api.get('/semesters', { params: { page: page.value, size: size.value } }); list.value = r.data.records; total.value = r.data.total } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '获取学期列表失败') }
  loading.value = false
}
function resetForm() { form.value = { name: '', code: '', startDate: '', endDate: '', isCurrent: false } }
async function save() {
  try {
    await api.post('/semesters', form.value)
    ElMessage.success('添加成功'); showAdd.value = false; resetForm(); fetchData()
  } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '添加失败') }
}
</script>
<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
</style>
