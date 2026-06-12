import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err.response?.data || err)
  }
)

export default api

// Auth
export const login = (data) => api.post('/auth/login', data)

// Users
export const getUsers = (params) => api.get('/users', { params })
export const getUser = (id) => api.get(`/users/${id}`)
export const addUser = (data) => api.post('/users', data)
export const updateUser = (data) => api.put('/users', data)
export const deleteUser = (id) => api.delete(`/users/${id}`)
export const getUsersByRole = (role) => api.get(`/users/role/${role}`)

// Courses
export const getCourses = (params) => api.get('/courses', { params })
export const getCourse = (id) => api.get(`/courses/${id}`)
export const addCourse = (data) => api.post('/courses', data)
export const updateCourse = (data) => api.put('/courses', data)
export const deleteCourse = (id) => api.delete(`/courses/${id}`)

// Selections
export const getSelections = (params) => api.get('/selections', { params })
export const selectCourse = (courseId) => api.post(`/selections/select/${courseId}`)
export const dropCourse = (courseId) => api.delete(`/selections/drop/${courseId}`)
export const getMySelections = () => api.get('/selections/my')

// Grades
export const getGrades = (params) => api.get('/grades', { params })
export const getMyGrades = () => api.get('/grades/my')
export const saveGrade = (data) => api.post('/grades', data)
export const updateGrade = (data) => api.put('/grades', data)
export const deleteGrade = (id) => api.delete(`/grades/${id}`)

// Announcements
export const getActiveAnnouncements = () => api.get('/announcements/active')
export const getAnnouncements = (params) => api.get('/announcements', { params })
export const addAnnouncement = (data) => api.post('/announcements', data)
export const updateAnnouncement = (data) => api.put('/announcements', data)
export const deleteAnnouncement = (id) => api.delete(`/announcements/${id}`)

// Dashboard
export const getDashboardStats = () => api.get('/dashboard/stats')

// Attendance
export const getAttendance = (params) => api.get('/attendance', { params })
export const saveAttendance = (data) => api.post('/attendance', data)
