<template>
  <div>
    <h2 style="margin-bottom:16px">学生管理</h2>
    <el-card>
      <el-button type="primary" @click="showAdd = true" style="margin-bottom:12px">添加学生</el-button>
      <el-table :data="users" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="操作" width="200" fixed="right">
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

    <el-dialog v-model="showAdd" :title="editing ? '编辑学生' : '添加学生'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="密码" v-if="!editing"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select>
        </el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
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
import { getUsers, addUser, updateUser, deleteUser } from '@/api'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const showAdd = ref(false)
const editing = ref(false)
const form = ref({ username: '', name: '', password: '', gender: '', phone: '', email: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getUsers({ page: page.value, size: size.value, role: 'student' })
    users.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* ignore */ }
  loading.value = false
}

function edit(row) {
  editing.value = true
  form.value = { ...row, password: '' }
  showAdd.value = true
}

function resetForm() {
  form.value = { username: '', name: '', password: '', gender: '', phone: '', email: '' }
  editing.value = false
}

async function save() {
  if (editing.value) {
    await updateUser(form.value)
  } else {
    form.value.role = 'student'
    await addUser(form.value)
  }
  showAdd.value = false
  resetForm()
  fetchData()
}

async function remove(id) {
  await deleteUser(id)
  fetchData()
}
</script>
