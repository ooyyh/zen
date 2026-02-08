import { createRouter, createWebHistory } from 'vue-router'
import { isAdmin, isLoggedIn } from '@/services/auth'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { public: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { public: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { title: '服务工作台', parent: '控制台' }
    },
    {
      path: '/classrooms',
      name: 'classrooms',
      component: () => import('../views/ClassroomListView.vue'),
      meta: { title: '教室资源', parent: '预约管理' }
    },
    {
      path: '/reservations/new',
      name: 'reservation-create',
      component: () => import('../views/ReservationCreateView.vue'),
      meta: { title: '发起预约', parent: '预约管理' }
    },
    {
      path: '/reservations/my',
      name: 'reservation-my',
      component: () => import('../views/ReservationListView.vue'),
      meta: { title: '我的预约', parent: '预约管理' }
    },
    {
      path: '/approvals',
      name: 'approvals',
      component: () => import('../views/ApprovalListView.vue'),
      meta: { title: '审批处理', parent: '后台管理', admin: true }
    },
    {
      path: '/lectures',
      name: 'lectures',
      component: () => import('../views/LectureListView.vue'),
      meta: { title: '讲座报名', parent: '讲座管理' }
    },
    {
      path: '/lectures/my',
      name: 'lecture-my',
      component: () => import('../views/LectureSignupListView.vue'),
      meta: { title: '我的讲座', parent: '讲座管理' }
    },
    {
      path: '/messages',
      name: 'messages',
      component: () => import('../views/MessageListView.vue'),
      meta: { title: '通知中心', parent: '消息中心' }
    },
    {
      path: '/admin/classrooms',
      name: 'admin-classrooms',
      component: () => import('../views/AdminClassroomView.vue'),
      meta: { title: '教室管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/lectures',
      name: 'admin-lectures',
      component: () => import('../views/AdminLectureView.vue'),
      meta: { title: '讲座管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('../views/AdminUserView.vue'),
      meta: { title: '用户管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/config',
      name: 'admin-config',
      component: () => import('../views/AdminConfigView.vue'),
      meta: { title: '规则配置', parent: '后台管理', admin: true }
    }
  ]
})

router.beforeEach((to) => {
  if (to.meta.public) {
    if (to.path === '/login' && isLoggedIn()) {
      return '/dashboard'
    }
    return true
  }
  if (!isLoggedIn()) {
    return '/login'
  }
  if (to.meta.admin && !isAdmin()) {
    return '/dashboard'
  }
  return true
})

export default router
