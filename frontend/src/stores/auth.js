import { defineStore } from 'pinia'
import { login as loginApi } from '@/api'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const rawToken = localStorage.getItem('token')
  const token = ref(rawToken && rawToken !== 'undefined' ? rawToken : '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')
  const name = ref(localStorage.getItem('name') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')
  const isTeacher = computed(() => role.value === 'teacher')
  const isStudent = computed(() => role.value === 'student')

  async function login(credentials) {
    const res = await loginApi(credentials)
    if (res.code !== 200) {
      throw new Error(res.message || '登录失败')
    }
    token.value = res.data.token
    username.value = res.data.username
    role.value = res.data.role
    name.value = res.data.name
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
    localStorage.setItem('name', res.data.name)
  }

  function logout() {
    token.value = ''
    username.value = ''
    role.value = ''
    name.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('name')
  }

  return { token, username, role, name, isLoggedIn, isAdmin, isTeacher, isStudent, login, logout }
})
