<template>
  <div class="dashboard">
    <h2 style="margin-bottom:20px">数据仪表盘</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:20px">
      <el-col :span="14">
        <el-card>
          <div ref="barRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <div ref="pieRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getDashboardStats, getUsersByRole } from '@/api'
import * as echarts from 'echarts'

const barRef = ref(null)
const pieRef = ref(null)
const stats = ref([
  { label: '学生人数', value: 0 },
  { label: '教师人数', value: 0 },
  { label: '课程数量', value: 0 },
  { label: '公告数量', value: 0 }
])

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    stats.value = [
      { label: '学生人数', value: res.data.studentCount },
      { label: '教师人数', value: res.data.teacherCount },
      { label: '课程数量', value: res.data.courseCount },
      { label: '公告数量', value: res.data.announcementCount }
    ]
  } catch (e) { /* ignore */ }

  nextTick(() => {
    if (barRef.value) {
      const bar = echarts.init(barRef.value)
      bar.setOption({
        title: { text: '各年级人数分布', left: 'center' },
        tooltip: {},
        xAxis: { type: 'category', data: ['大一', '大二', '大三', '大四'] },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: [45, 52, 38, 15], itemStyle: { color: '#5470c6' } }]
      })
    }
    if (pieRef.value) {
      const pie = echarts.init(pieRef.value)
      pie.setOption({
        title: { text: '用户角色分布', left: 'center' },
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: '60%',
          data: [
            { value: stats.value[0].value, name: '学生' },
            { value: stats.value[1].value, name: '教师' },
            { value: 1, name: '管理员' }
          ]
        }]
      })
    }
  })
})
</script>

<style scoped>
.stat-value { font-size: 32px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #94a3b8; margin-top: 4px; }
</style>
