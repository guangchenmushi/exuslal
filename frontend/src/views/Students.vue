<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="page-desc">学生与教师信息管理</p>
    </div>
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="学生管理" name="student">
          <div class="toolbar">
            <el-button type="primary" @click="showAdd = true">添加学生</el-button>
            <el-button @click="exportExcel">导出 CSV</el-button>
            <el-upload :show-file-list="false" :before-upload="importExcel" accept=".csv">
              <el-button>导入 CSV</el-button>
            </el-upload>
          </div>
          <el-table :data="users" border stripe v-loading="loading" style="width:100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="username" label="学号" width="110" />
            <el-table-column prop="name" label="姓名" width="90" />
            <el-table-column prop="gender" label="性别" width="60" />
            <el-table-column prop="phone" label="电话" width="130" />
            <el-table-column prop="email" label="邮箱" min-width="160" />
            <el-table-column prop="hobby" label="爱好" width="120" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="edit(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev,pager,next" @current-change="fetchData" style="margin-top:12px;justify-content:center" />
        </el-tab-pane>
        <el-tab-pane label="教师管理" name="teacher">
          <div class="toolbar">
            <el-button type="primary" @click="showAddTeacher = true">添加教师</el-button>
          </div>
          <el-table :data="teachers" border stripe v-loading="loadingTeacher" style="width:100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="username" label="工号" width="110" />
            <el-table-column prop="name" label="姓名" width="90" />
            <el-table-column prop="gender" label="性别" width="60" />
            <el-table-column prop="phone" label="电话" width="130" />
            <el-table-column prop="email" label="邮箱" min-width="160" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="editTeacher(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="removeTeacher(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="showAdd" :title="editing ? '编辑学生' : '添加学生'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="密码" v-if="!editing"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="爱好"><el-input v-model="form.hobby" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddTeacher" :title="editingTeacher ? '编辑教师' : '添加教师'" width="500px">
      <el-form :model="teacherForm" label-width="80px">
        <el-form-item label="工号"><el-input v-model="teacherForm.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="teacherForm.name" /></el-form-item>
        <el-form-item label="密码" v-if="!editingTeacher"><el-input v-model="teacherForm.password" type="password" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="teacherForm.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
        <el-form-item label="电话"><el-input v-model="teacherForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="teacherForm.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddTeacher = false">取消</el-button>
        <el-button type="primary" @click="saveTeacher">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, addUser, updateUser, deleteUser } from '@/api'
import api from '@/api'
import { ElMessage } from 'element-plus'

const activeTab = ref('student')
const users = ref([]); const teachers = ref([])
const loading = ref(false); const loadingTeacher = ref(false)
const page = ref(1); const size = ref(20); const total = ref(0)
const showAdd = ref(false); const editing = ref(false)
const showAddTeacher = ref(false); const editingTeacher = ref(false)
const form = ref({ username: '', name: '', password: '', gender: '', phone: '', email: '', hobby: '' })
const teacherForm = ref({ username: '', name: '', password: '', gender: '', phone: '', email: '' })

onMounted(() => { fetchData(); fetchTeachers() })

async function fetchData() {
  loading.value = true
  try { const res = await getUsers({ page: page.value, size: size.value, role: 'student' }); users.value = res.data.records; total.value = res.data.total } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '获取学生列表失败') }
  loading.value = false
}
async function fetchTeachers() {
  loadingTeacher.value = true
  try { const res = await getUsers({ page: 1, size: 100, role: 'teacher' }); teachers.value = res.data.records } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '获取教师列表失败') }
  loadingTeacher.value = false
}

function edit(row) { editing.value = true; form.value = { ...row, password: '' }; showAdd.value = true }
function editTeacher(row) { editingTeacher.value = true; teacherForm.value = { ...row, password: '' }; showAddTeacher.value = true }
function resetForm() { form.value = { username: '', name: '', password: '', gender: '', phone: '', email: '', hobby: '' }; editing.value = false }
function resetTeacherForm() { teacherForm.value = { username: '', name: '', password: '', gender: '', phone: '', email: '' }; editingTeacher.value = false }

async function save() {
  try {
    if (editing.value) await updateUser(form.value); else { form.value.role = 'student'; await addUser(form.value) }
    ElMessage.success('保存成功'); showAdd.value = false; resetForm(); fetchData()
  } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '保存失败') }
}
async function saveTeacher() {
  try {
    if (editingTeacher.value) await updateUser(teacherForm.value); else { teacherForm.value.role = 'teacher'; await addUser(teacherForm.value) }
    ElMessage.success('保存成功'); showAddTeacher.value = false; resetTeacherForm(); fetchTeachers()
  } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '保存失败') }
}
async function remove(id) {
  try { await deleteUser(id); ElMessage.success('删除成功'); fetchData() } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '删除失败') }
}
async function removeTeacher(id) {
  try { await deleteUser(id); ElMessage.success('删除成功'); fetchTeachers() } catch(e) { ElMessage.error(e?.response?.data?.message || e?.message || '删除失败') }
}

async function exportExcel() {
  try {
    const res = await api.get('/users/export', { params: { role: 'student' }, responseType: 'blob' })
    const url = URL.createObjectURL(new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
    const a = document.createElement('a'); a.href = url; a.download = 'students.xlsx'; a.click()
    URL.revokeObjectURL(url)
  } catch(e) { ElMessage.error('导出失败') }
}
async function importExcel(file) {
  const formData = new FormData(); formData.append('file', file)
  try { await api.post('/users/import', formData); fetchData(); ElMessage.success('导入成功') } catch(e) { ElMessage.error('导入失败') }
  return false
}
</script>
<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.page-desc { font-size: 13px; color: #64748b; margin-top: 4px; }
.toolbar { display: flex; gap: 10px; margin-bottom: 12px; align-items: center; }
.toolbar .el-upload { display: inline-flex; }
</style>
