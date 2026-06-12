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
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue') }
    ]
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
  } else if (to.path === '/login' && auth.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
