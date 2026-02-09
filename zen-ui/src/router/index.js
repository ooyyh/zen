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
      path: '/equipments',
      name: 'equipments',
      component: () => import('../views/EquipmentListView.vue'),
      meta: { title: '设备借用', parent: '设备管理' }
    },
    {
      path: '/equipments/my',
      name: 'equipments-my',
      component: () => import('../views/EquipmentBorrowListView.vue'),
      meta: { title: '我的设备', parent: '设备管理' }
    },
    {
      path: '/bus/trips',
      name: 'bus-trips',
      component: () => import('../views/BusTripListView.vue'),
      meta: { title: '校车预约', parent: '校车管理' }
    },
    {
      path: '/bus/bookings/my',
      name: 'bus-bookings-my',
      component: () => import('../views/BusBookingListView.vue'),
      meta: { title: '我的校车', parent: '校车管理' }
    },
    {
      path: '/study-rooms',
      name: 'study-rooms',
      component: () => import('../views/StudyRoomListView.vue'),
      meta: { title: '自习室抢座', parent: '自习管理' }
    },
    {
      path: '/study-rooms/:id/seats',
      name: 'seat-selection',
      component: () => import('../views/SeatSelectionView.vue'),
      meta: { title: '选择座位', parent: '自习管理' }
    },
    {
      path: '/seat-reservations/my',
      name: 'seat-reservations-my',
      component: () => import('../views/SeatReservationListView.vue'),
      meta: { title: '我的座位', parent: '自习管理' }
    },
    {
      path: '/admin/reports',
      name: 'admin-reports',
      component: () => import('../views/AdminReportView.vue'),
      meta: { title: '运营报表', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/classrooms',
      name: 'admin-classrooms',
      component: () => import('../views/AdminClassroomView.vue'),
      meta: { title: '教室管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/equipments',
      name: 'admin-equipments',
      component: () => import('../views/AdminEquipmentView.vue'),
      meta: { title: '设备管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/equipments/borrows',
      name: 'admin-equipments-borrows',
      component: () => import('../views/AdminEquipmentBorrowView.vue'),
      meta: { title: '借用审批', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/bus/routes',
      name: 'admin-bus-routes',
      component: () => import('../views/AdminBusRouteView.vue'),
      meta: { title: '线路管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/bus/trips',
      name: 'admin-bus-trips',
      component: () => import('../views/AdminBusTripView.vue'),
      meta: { title: '班次管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/bus/bookings',
      name: 'admin-bus-bookings',
      component: () => import('../views/AdminBusBookingView.vue'),
      meta: { title: '校车预约记录', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/lectures',
      name: 'admin-lectures',
      component: () => import('../views/AdminLectureView.vue'),
      meta: { title: '讲座管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/lectures/checkins',
      name: 'admin-lecture-checkins',
      component: () => import('../views/AdminLectureCheckinView.vue'),
      meta: { title: '讲座签到', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('../views/AdminUserView.vue'),
      meta: { title: '用户管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/broadcasts',
      name: 'admin-broadcasts',
      component: () => import('../views/AdminBroadcastView.vue'),
      meta: { title: '通知发布', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/message-templates',
      name: 'admin-message-templates',
      component: () => import('../views/AdminMessageTemplateView.vue'),
      meta: { title: '消息模板', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/config',
      name: 'admin-config',
      component: () => import('../views/AdminConfigView.vue'),
      meta: { title: '规则配置', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/study-rooms',
      name: 'admin-study-rooms',
      component: () => import('../views/AdminStudyRoomView.vue'),
      meta: { title: '自习室管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/study-rooms/:id/seats',
      name: 'admin-seat-management',
      component: () => import('../views/AdminSeatManagementView.vue'),
      meta: { title: '座位管理', parent: '后台管理', admin: true }
    },
    {
      path: '/admin/seat-reservations',
      name: 'admin-seat-reservations',
      component: () => import('../views/AdminSeatReservationView.vue'),
      meta: { title: '座位预约记录', parent: '后台管理', admin: true }
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
