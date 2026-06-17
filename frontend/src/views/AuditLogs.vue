<template>
  <div>
    <div class="page-header"><h2>审计日志</h2><p class="page-desc">系统操作记录</p></div>
    <el-card>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="action" label="操作" width="150" />
        <el-table-column prop="target" label="目标" width="120" />
        <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column label="时间" width="170"><template #default="{row}">{{ (row.createTime || '').replace('T',' ') }}</template></el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev,pager,next" @current-change="fetchData" style="margin-top:12px;justify-content:center" />
    </el-card>
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
onMounted(() => fetchData())
async function fetchData() {
  loading.value = true
  try { const r = await api.get('/audit-logs', { params: { page: page.value, size: size.value } }); list.value = r.data.records; total.value = r.data.total } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '获取审计日志失败') }
  loading.value = false
}
</script>
<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
</style>
