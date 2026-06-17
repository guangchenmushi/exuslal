import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'students', name: 'Students', component: () => import('@/views/Students.vue'), meta: { roles: ['admin'] } },
      { path: 'courses', name: 'Courses', component: () => import('@/views/Courses.vue') },
      { path: 'selections', name: 'Selections', component: () => import('@/views/Selections.vue') },
      { path: 'grades', name: 'Grades', component: () => import('@/views/Grades.vue') },
      { path: 'announcements', name: 'Announcements', component: () => import('@/views/Announcements.vue') },
      { path: 'attendance', name: 'Attendance', component: () => import('@/views/Attendance.vue') },
      { path: 'homework', name: 'Homework', component: () => import('@/views/Homework.vue') },
      { path: 'exams', name: 'Exams', component: () => import('@/views/Exams.vue') },
      { path: 'audit-logs', name: 'AuditLogs', component: () => import('@/views/AuditLogs.vue') },
      { path: 'login-logs', name: 'LoginLogs', component: () => import('@/views/LoginLogs.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue') },
      { path: 'classes', name: 'Classes', component: () => import('@/views/Classes.vue'), meta: { roles: ['admin'] } },
      { path: 'semesters', name: 'Semesters', component: () => import('@/views/Semesters.vue'), meta: { roles: ['admin'] } }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    next('/login')
  } else if (auth.isLoggedIn && (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password')) {
    next('/dashboard')
  } else if (to.meta.roles && (!auth.role || !to.meta.roles.includes(auth.role))) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
