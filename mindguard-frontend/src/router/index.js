import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const counselorChildren = [
  { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
  { path: 'alerts', name: 'AlertManagement', component: () => import('@/views/AlertManagement.vue') },
  { path: 'alerts/:id', name: 'AlertDetail', component: () => import('@/views/AlertDetail.vue') },
  { path: 'appointments', name: 'AppointmentReview', component: () => import('@/views/AppointmentReview.vue') },
  { path: 'appointments/:id', name: 'AppointmentDetail', component: () => import('@/views/AppointmentDetail.vue') },
  { path: 'students/:id', name: 'StudentArchive', component: () => import('@/views/StudentArchive.vue') },
  { path: 'articles', name: 'ArticleManagement', component: () => import('@/views/ArticleManagement.vue') },
  { path: 'assessments', name: 'AssessmentData', component: () => import('@/views/AssessmentData.vue') },
  { path: 'profile', name: 'CounselorProfile', component: () => import('@/views/PersonalCenter.vue') },
  { path: 'notifications', name: 'CounselorNotifications', component: () => import('@/views/NotificationList.vue') }
]

const adminChildren = [
  ...counselorChildren.map(c => ({ path: c.path, component: c.component })),
  { path: 'settings', name: 'AISettings', component: () => import('@/views/AISettings.vue') }
]

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/student',
    component: () => import('@/views/Layout.vue'),
    meta: { role: 'STUDENT' },
    redirect: '/student/home',
    children: [
      { path: 'home', name: 'StudentHome', component: () => import('@/views/Home.vue') },
      { path: 'emotion-square', name: 'EmotionSquare', component: () => import('@/views/EmotionSquare.vue') },
      { path: 'posts', name: 'MyPosts', component: () => import('@/views/MyPosts.vue') },
      { path: 'ai-chat', name: 'AIChat', component: () => import('@/views/AIChat.vue') },
      { path: 'checkin', name: 'MoodCheckin', component: () => import('@/views/MoodCheckin.vue') },
      { path: 'assessments', name: 'AssessmentList', component: () => import('@/views/AssessmentList.vue') },
      { path: 'assessments/:id', name: 'TakeAssessment', component: () => import('@/views/TakeAssessment.vue') },
      { path: 'assessments/my', name: 'MyAssessments', component: () => import('@/views/MyAssessments.vue') },
      { path: 'counselors', name: 'CounselorList', component: () => import('@/views/CounselorList.vue') },
      { path: 'counselors/:id/book', name: 'BookAppointment', component: () => import('@/views/BookAppointment.vue') },
      { path: 'appointments', name: 'MyAppointments', component: () => import('@/views/MyAppointments.vue') },
      { path: 'articles', name: 'Articles', component: () => import('@/views/Articles.vue') },
      { path: 'articles/:id', name: 'ArticleDetail', component: () => import('@/views/ArticleDetail.vue') },
      { path: 'profile', name: 'StudentProfile', component: () => import('@/views/PersonalCenter.vue') },
      { path: 'notifications', name: 'StudentNotifications', component: () => import('@/views/NotificationList.vue') }
    ]
  },
  {
    path: '/counselor',
    component: () => import('@/views/Layout.vue'),
    meta: { role: 'COUNSELOR' },
    redirect: '/counselor/dashboard',
    children: counselorChildren
  },
  {
    path: '/admin',
    component: () => import('@/views/Layout.vue'),
    meta: { role: 'ADMIN' },
    redirect: '/admin/dashboard',
    children: adminChildren
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const adminRoles = ['COUNSELOR', 'ADMIN']

router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.meta.public) {
    if (token && (to.path === '/login' || to.path === '/register')) {
      const role = localStorage.getItem('userRole')
      if (role === 'STUDENT') return next('/student/home')
      if (role === 'COUNSELOR') return next('/counselor/dashboard')
      if (role === 'ADMIN') return next('/admin/dashboard')
      return next('/login')
    }
    return next()
  }
  if (!token) {
    return next('/login')
  }
  const role = localStorage.getItem('userRole')
  if (to.meta.role && to.meta.role !== role) {
    if (role === 'ADMIN') return next()
    if (role === 'STUDENT') return next('/student/home')
    if (adminRoles.includes(role)) return next('/counselor/dashboard')
    return next('/login')
  }
  next()
})

export default router
