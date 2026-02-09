<script setup>
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { clearAuth, getUser, isAdmin } from '@/services/auth'

const props = defineProps({
  title: {
    type: String,
    default: ''
  }
})

const route = useRoute()
const router = useRouter()
const user = getUser()
const admin = computed(() => isAdmin())

const text = {
  brandSub: '服务控制台',
  core: '核心功能',
  admin: '后台管理',
  breadcrumb: '控制台',
  logout: '退出登录',
  unknownUser: '未知用户',
  nav: {
    dashboard: '工作台',
    classrooms: '教室资源',
    reservationNew: '发起预约',
    reservationMy: '我的预约',
    lectures: '讲座报名',
    lecturesMy: '我的讲座',
    equipments: '设备借用',
    equipmentsMy: '我的设备',
    busTrips: '校车预约',
    busMy: '我的校车',
    studyRooms: '自习室抢座',
    seatMy: '我的座位',
    messages: '通知中心'
  },
  adminNav: {
    approvals: '审批处理',
    reports: '运营报表',
    classrooms: '教室管理',
    lectures: '讲座管理',
    lectureCheckin: '讲座签到',
    equipments: '设备管理',
    equipmentBorrow: '借用审批',
    busRoutes: '校车线路',
    busTrips: '校车班次',
    busBookings: '校车记录',
    studyRooms: '自习室管理',
    seatReservations: '座位预约记录',
    users: '用户管理',
    broadcasts: '通知发布',
    templates: '消息模板',
    config: '规则配置'
  }
}

const storedSections = (() => {
  try {
    return JSON.parse(localStorage.getItem('zen_sidebar_sections') || '{}')
  } catch (e) {
    return {}
  }
})()

// 管理员默认收起核心功能，普通用户默认展开
const sections = ref({
  core: storedSections.core !== undefined ? storedSections.core : !admin.value,
  admin: storedSections.admin !== false
})

const toggleSection = (key) => {
  sections.value[key] = !sections.value[key]
  try {
    localStorage.setItem('zen_sidebar_sections', JSON.stringify(sections.value))
  } catch (e) {
    // ignore storage errors
  }
}

const logout = () => {
  clearAuth()
  router.push('/login')
}

const navItems = [
  { label: text.nav.dashboard, to: '/dashboard', icon: 'M4 4h7v7H4V4zm9 0h7v4h-7V4zM4 13h7v7H4v-7zm9 5h7v2h-7v-2z' },
  { label: text.nav.classrooms, to: '/classrooms', icon: 'M4 5h16v5H4V5zm0 7h10v7H4v-7zm12 0h4v7h-4v-7z' },
  { label: text.nav.reservationNew, to: '/reservations/new', icon: 'M5 4h14v2H5V4zm0 4h14v12H5V8zm4 3h6v2H9v-2zm0 4h6v2H9v-2z' },
  { label: text.nav.reservationMy, to: '/reservations/my', icon: 'M6 4h12v4H6V4zm0 6h12v10H6V10z' },
  { label: text.nav.lectures, to: '/lectures', icon: 'M4 6h16v8H4V6zm3-3h10v2H7V3zm-1 13h12v2H6v-2z' },
  { label: text.nav.lecturesMy, to: '/lectures/my', icon: 'M6 4h12v4H6V4zm0 6h12v10H6V10z' },
  { label: text.nav.equipments, to: '/equipments', icon: 'M6 4h12v4H6V4zm-2 6h16v8H4v-8z' },
  { label: text.nav.equipmentsMy, to: '/equipments/my', icon: 'M4 5h16v14H4V5zm3 3h10v2H7V8zm0 4h6v2H7v-2z' },
  { label: text.nav.busTrips, to: '/bus/trips', icon: 'M5 6h14l1.5 4.5V16a2 2 0 0 1-2 2H17a2 2 0 0 1-2-2H9a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-5.5L5 6zm1 5h12v3H6v-3z' },
  { label: text.nav.busMy, to: '/bus/bookings/my', icon: 'M4 6h16v10H4V6zm3 2v6h2V8H7zm4 0v6h2V8h-2z' },
  { label: text.nav.studyRooms, to: '/study-rooms', icon: 'M3 3h18v2H3V3zm0 4h8v14H3V7zm10 0h8v14h-8V7z' },
  { label: text.nav.seatMy, to: '/seat-reservations/my', icon: 'M4 4h16v4H4V4zm0 6h7v10H4V10zm9 0h7v10h-7V10z' },
  { label: text.nav.messages, to: '/messages', icon: 'M12 4a5 5 0 0 1 5 5v4l2 2H5l2-2V9a5 5 0 0 1 5-5z' }
]

const adminItems = [
  { label: text.adminNav.approvals, to: '/approvals', icon: 'M6 4h12v2H6V4zm0 5h12v2H6V9zm0 5h12v2H6v-2z' },
  { label: text.adminNav.reports, to: '/admin/reports', icon: 'M3 3h18v2H3V3zm2 4h3v10H5V7zm5 4h3v6h-3v-6zm5-2h3v8h-3V9z' },
  { label: text.adminNav.classrooms, to: '/admin/classrooms', icon: 'M4 6h16v12H4V6zm3 2v8h2V8H7zm4 0v8h2V8h-2z' },
  { label: text.adminNav.lectures, to: '/admin/lectures', icon: 'M4 6h16v8H4V6zm3-3h10v2H7V3zm-1 13h12v2H6v-2z' },
  { label: text.adminNav.lectureCheckin, to: '/admin/lectures/checkins', icon: 'M6 4h12v2H6V4zm-1 4h14v2H5V8zm1 4h10v2H6v-2z' },
  { label: text.adminNav.equipments, to: '/admin/equipments', icon: 'M6 4h12v4H6V4zm-2 6h16v8H4v-8z' },
  { label: text.adminNav.equipmentBorrow, to: '/admin/equipments/borrows', icon: 'M6 4h12v2H6V4zm0 5h12v2H6V9zm0 5h12v2H6v-2z' },
  { label: text.adminNav.busRoutes, to: '/admin/bus/routes', icon: 'M4 5h16v2H4V5zm0 6h16v2H4v-2zm0 6h16v2H4v-2z' },
  { label: text.adminNav.busTrips, to: '/admin/bus/trips', icon: 'M5 6h14l1.5 4.5V16a2 2 0 0 1-2 2H17a2 2 0 0 1-2-2H9a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-5.5L5 6zm1 5h12v3H6v-3z' },
  { label: text.adminNav.busBookings, to: '/admin/bus/bookings', icon: 'M6 4h12v2H6V4zm0 5h12v2H6V9zm0 5h12v2H6v-2z' },
  { label: text.adminNav.studyRooms, to: '/admin/study-rooms', icon: 'M3 3h18v2H3V3zm0 4h8v14H3V7zm10 0h8v14h-8V7z' },
  { label: text.adminNav.seatReservations, to: '/admin/seat-reservations', icon: 'M4 4h16v4H4V4zm0 6h7v10H4V10zm9 0h7v10h-7V10z' },
  { label: text.adminNav.users, to: '/admin/users', icon: 'M7 10a3 3 0 1 1 6 0 3 3 0 0 1-6 0zm-2 8a5 5 0 0 1 10 0H5z' },
  { label: text.adminNav.broadcasts, to: '/admin/broadcasts', icon: 'M4 5h16v10H4V5zm3 2h10v2H7V7zm0 4h7v2H7v-2z' },
  { label: text.adminNav.templates, to: '/admin/message-templates', icon: 'M4 4h16v12H4V4zm3 3h10v2H7V7zm0 4h7v2H7v-2z' },
  { label: text.adminNav.config, to: '/admin/config', icon: 'M4 4h16v4H4V4zm0 6h16v10H4V10zm4 2h8v2H8v-2zm0 4h5v2H8v-2z' }
]
</script>

<template>
  <div class="shell">
    <aside class="sidebar">
      <RouterLink to="/" class="brand">
        <div class="brand-mark">
          <span class="brand-dot"></span>
        </div>
        <div>
          <p class="brand-title">Zen Campus</p>
          <p class="brand-sub">{{ text.brandSub }}</p>
        </div>
      </RouterLink>

      <div class="nav-section">
        <button class="nav-toggle" type="button" @click="toggleSection('core')" :aria-expanded="sections.core">
          <span>{{ text.core }}</span>
          <svg class="chevron" :class="{ rotated: !sections.core }" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M6 9l6 6 6-6" fill="currentColor" />
          </svg>
        </button>
        <div class="nav-items" :class="{ collapsed: !sections.core }" :aria-hidden="!sections.core">
          <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" class="nav-item">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path :d="item.icon" fill="currentColor" />
            </svg>
            <span>{{ item.label }}</span>
          </RouterLink>
        </div>
      </div>

      <div v-if="admin" class="nav-section">
        <button class="nav-toggle" type="button" @click="toggleSection('admin')" :aria-expanded="sections.admin">
          <span>{{ text.admin }}</span>
          <svg class="chevron" :class="{ rotated: !sections.admin }" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M6 9l6 6 6-6" fill="currentColor" />
          </svg>
        </button>
        <div class="nav-items" :class="{ collapsed: !sections.admin }" :aria-hidden="!sections.admin">
          <RouterLink v-for="item in adminItems" :key="item.to" :to="item.to" class="nav-item">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path :d="item.icon" fill="currentColor" />
            </svg>
            <span>{{ item.label }}</span>
          </RouterLink>
        </div>
      </div>
    </aside>

    <div class="content">
      <header class="content-header">
        <div>
          <p class="breadcrumb">{{ route.meta.parent || text.breadcrumb }}</p>
          <h1>{{ props.title || route.meta.title }}</h1>
        </div>
        <div class="user-area">
          <div class="user-chip">
            <span>{{ user?.username || text.unknownUser }}</span>
            <em>{{ user?.role || 'UNKNOWN' }}</em>
          </div>
          <button class="btn ghost" @click="logout">{{ text.logout }}</button>
        </div>
      </header>

      <div class="content-body">
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
  background: #eef2f7;
}

.sidebar {
  background: #ffffff;
  border-right: 1px solid var(--border);
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: linear-gradient(135deg, #1d4ed8, #0ea5e9);
  display: grid;
  place-items: center;
  box-shadow: var(--shadow-sm);
}

.brand-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #f8fafc;
}

.brand-title {
  font-weight: 600;
}

.brand-sub {
  font-size: 12px;
  color: var(--text-muted);
}

.nav-section {
  display: grid;
  gap: 10px;
}

.nav-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  width: 100%;
  background: transparent;
  border: none;
  padding: 0;
  font-size: 12px;
  color: var(--text-muted);
  letter-spacing: 0.12em;
  cursor: pointer;
}

.nav-toggle span {
  text-align: left;
}

.chevron {
  width: 14px;
  height: 14px;
  color: var(--text-light);
  transition: transform 0.2s ease;
}

.chevron.rotated {
  transform: rotate(-90deg);
}

.nav-items {
  display: grid;
  gap: 6px;
}

.nav-items.collapsed {
  display: none;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  color: var(--text-muted);
  transition: all 0.2s ease;
}

.nav-item svg {
  width: 18px;
  height: 18px;
}

.nav-item.router-link-exact-active,
.nav-item:hover {
  color: var(--primary);
  background: rgba(29, 78, 216, 0.12);
}

.content {
  display: flex;
  flex-direction: column;
  padding: 24px 32px 48px;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}

.content-header h1 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 28px;
  margin-top: 6px;
}

.breadcrumb {
  font-size: 12px;
  color: var(--text-light);
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.06);
  font-size: 13px;
}

.user-chip em {
  font-style: normal;
  color: var(--text-muted);
}

.content-body {
  display: grid;
  gap: 20px;
}

@media (max-width: 1100px) {
  .shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: sticky;
    top: 0;
    z-index: 10;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .nav-section {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .content {
    padding: 20px;
  }
}

@media (max-width: 720px) {
  .content-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
