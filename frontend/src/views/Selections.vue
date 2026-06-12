<template>
  <div>
    <h2 style="margin-bottom:16px">选课中心</h2>
    <el-row :gutter="16">
      <el-col :span="12" v-for="c in courses" :key="c.id" style="margin-bottom:16px">
        <el-card shadow="hover">
          <div class="course-name">{{ c.name }}</div>
          <div class="course-info">编号：{{ c.code }} | 学分：{{ c.credit }} | 容量：{{ c.maxStudents }}</div>
          <div class="course-info">教室：{{ c.classroom }} | 时间：{{ c.schedule }}</div>
          <el-button
            :type="selectedIds.has(c.id) ? 'danger' : 'primary'"
            size="small"
            style="margin-top:8px"
            @click="toggle(c.id)">
            {{ selectedIds.has(c.id) ? '退选' : '选课' }}
          </el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="courses.length === 0" description="暂无可选课程" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourses, selectCourse, dropCourse, getMySelections } from '@/api'

const courses = ref([])
const selectedIds = ref(new Set())

onMounted(async () => {
  try {
    const res = await getCourses({ page: 1, size: 100 })
    courses.value = res.data.records
  } catch (e) { /* ignore */ }
  try {
    const my = await getMySelections()
    my.data.forEach(s => selectedIds.value.add(s.courseId))
  } catch (e) { /* ignore */ }
})

async function toggle(courseId) {
  try {
    if (selectedIds.value.has(courseId)) {
      await dropCourse(courseId)
      selectedIds.value.delete(courseId)
    } else {
      await selectCourse(courseId)
      selectedIds.value.add(courseId)
    }
  } catch (e) {
    alert(e?.message || '操作失败')
  }
}
</script>

<style scoped>
.course-name { font-size: 16px; font-weight: bold; margin-bottom: 6px; }
.course-info { font-size: 13px; color: #64748b; }
</style>
