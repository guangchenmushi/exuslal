<template>
  <div>
    <div class="page-header"><h2>登录日志</h2><p class="page-desc">用户登录记录</p></div>
    <el-card>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="success" label="状态" width="70">
          <template #default="{row}"><el-tag :type="row.success===1?'success':'danger'">{{ row.success===1?'成功':'失败' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="时间" width="170"><template #default="{row}">{{ (row.loginTime || '').replace('T',' ') }}</template></el-table-column>
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
  try { const r = await api.get('/login-logs', { params: { page: page.value, size: size.value } }); list.value = r.data.records; total.value = r.data.total } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '获取登录日志失败') }
  loading.value = false
}
</script>
<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
</style>
