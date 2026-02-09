<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import MarketingLayout from '@/components/MarketingLayout.vue'
import { request } from '@/services/api'
import { isLoggedIn } from '@/services/auth'

const text = {
  badge: '校园综合服务系统 · 实时数据',
  heroTitle: '一个入口，统一预约、审批与消息触达。',
  heroSub: '教室、讲座、设备与校车服务统一管理，真实数据实时同步。',
  enterConsole: '进入控制台',
  startReservation: '发起预约',
  summary: {
    classroom: '教室资源',
    equipment: '设备资源',
    lecture: '开放讲座',
    bus: '开放班次'
  },
  badges: {
    pendingApproval: '待审批',
    reservationPending: '预约待审'
  },
  hot: {
    title: '热门教室',
    desc: '按预约次数排序',
    refresh: '刷新',
    empty: '暂无数据',
    approvedTitle: '当前预约通过',
    approvedDesc: '已批准',
    approvedUnit: '条',
    status: '实时',
    times: '次',
    capacity: '容量',
    locationEmpty: '未填写位置'
  },
  services: {
    title: '服务入口',
    sub: '6 大高频业务直达'
  },
  lectures: {
    title: '近期讲座',
    sub: '开放报名讲座',
    empty: '暂无讲座',
    viewAll: '查看全部讲座',
    locationEmpty: '未填写地点',
    capacity: '容量'
  },
  highlights: {
    title: '系统亮点',
    sub: '已上线能力'
  },
  notices: {
    title: '通知中心',
    sub: '最新站内消息',
    login: '登录后查看你的消息',
    goLogin: '去登录',
    empty: '暂无消息',
    view: '进入消息中心',
    error: '消息加载失败'
  },
  errors: {
    load: '加载失败'
  }
}

const services = [
  {
    title: '教室预约',
    desc: '按楼栋、容量、设备筛选教室，实时锁定空闲时段。',
    tag: '实时空闲',
    to: '/classrooms',
    icon: 'M4 4h16v4H4V4zm0 6h16v10H4V10zm4 2v6h2v-6H8zm6 0v6h2v-6h-2z'
  },
  {
    title: '讲座报名',
    desc: '自动候补、签到与通知同步，报名状态一目了然。',
    tag: '自动候补',
    to: '/lectures',
    icon: 'M4 6h16v8H4V6zm3-3h10v2H7V3zm-1 13h12v2H6v-2z'
  },
  {
    title: '设备借用',
    desc: '器材借用、审批与归还全流程闭环。',
    tag: '审批闭环',
    to: '/equipments',
    icon: 'M7 4h10l2 4v9a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V8l2-4zm1 6h8v2H8v-2z'
  },
  {
    title: '校车预约',
    desc: '班次余位可视化，支持候补与自动递补。',
    tag: '余位可视',
    to: '/bus/trips',
    icon: 'M5 6h14l1.5 4.5V16a2 2 0 0 1-2 2H17a2 2 0 0 1-2-2H9a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-5.5L5 6zm1 5h12v3H6v-3z'
  },
  {
    title: '审批中心',
    desc: '教室、设备、讲座等审批统一处理。',
    tag: '流程统一',
    to: '/approvals',
    icon: 'M6 4h9l3 3v11a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2zm0 6h12v2H6v-2zm0 4h12v2H6v-2z'
  },
  {
    title: '通知中心',
    desc: '站内消息模板化，广播与提醒及时触达。',
    tag: '模板化',
    to: '/messages',
    icon: 'M12 4a5 5 0 0 1 5 5v4l2 2H5l2-2V9a5 5 0 0 1 5-5zm-3 14h6a3 3 0 0 1-6 0z'
  }
]

const highlights = [
  {
    title: '分布式锁防冲突',
    desc: '高并发预约自动加锁，避免重复占用。'
  },
  {
    title: '候补与自动递补',
    desc: '讲座与校车候补自动转正，释放名额及时填补。'
  },
  {
    title: '审批链可视化',
    desc: '审批流程清晰可追踪，支持及时提醒。'
  },
  {
    title: '消息模板驱动',
    desc: '模板化通知减少重复沟通，触达更稳定。'
  }
]

const loading = ref(false)
const error = ref('')
const overview = ref(null)
const messages = ref([])
const messageError = ref('')
const loggedIn = computed(() => isLoggedIn())

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    overview.value = await request('/api/home/overview')
  } catch (e) {
    error.value = e.message || text.errors.load
  } finally {
    loading.value = false
  }
}

const loadMessages = async () => {
  if (!loggedIn.value) return
  messageError.value = ''
  try {
    const data = await request('/api/messages')
    messages.value = data.slice(0, 3)
  } catch (e) {
    messageError.value = e.message || text.notices.error
  }
}

const summaryCards = computed(() => {
  const data = overview.value || {}
  return [
    { label: text.summary.classroom, value: data.classroomCount || 0 },
    { label: text.summary.equipment, value: data.equipmentCount || 0 },
    { label: text.summary.lecture, value: data.lectureOpenCount || 0 },
    { label: text.summary.bus, value: data.busTripOpenCount || 0 }
  ]
})

const hotClassrooms = computed(() => (overview.value?.hotClassrooms || []))
const upcomingLectures = computed(() => (overview.value?.upcomingLectures || []))

const formatTime = (value) => {
  if (!value) return '-'
  return value.replace('T', ' ')
}

onMounted(async () => {
  await load()
  await loadMessages()
})
</script>

<template>
  <MarketingLayout>
    <div class="page">
      <section class="hero container">
        <div class="hero-copy">
          <span class="pill">{{ text.badge }}</span>
          <h1>{{ text.heroTitle }}</h1>
          <p class="subtle">
            {{ text.heroSub }}
          </p>
          <div class="hero-actions">
            <RouterLink class="btn primary" to="/dashboard">{{ text.enterConsole }}</RouterLink>
            <RouterLink class="btn ghost" to="/reservations/new">{{ text.startReservation }}</RouterLink>
          </div>
          <div class="hero-metrics">
            <div v-for="item in summaryCards" :key="item.label">
              <p class="metric-value">{{ item.value }}</p>
              <p class="metric-label">{{ item.label }}</p>
            </div>
          </div>
          <div v-if="overview" class="hero-badges">
            <span class="tag">{{ text.badges.pendingApproval }} {{ overview.pendingApprovalCount || 0 }}</span>
            <span class="tag">{{ text.badges.reservationPending }} {{ overview.reservationPendingCount || 0 }}</span>
          </div>
        </div>

        <div class="hero-panel card">
          <div class="panel-header">
            <div>
              <h3>{{ text.hot.title }}</h3>
              <p class="subtle">{{ text.hot.desc }}</p>
            </div>
            <button class="btn ghost small" type="button" @click="load">{{ text.hot.refresh }}</button>
          </div>
          <div class="panel-list" v-if="hotClassrooms.length">
            <div v-for="item in hotClassrooms" :key="item.id" class="panel-row">
              <div>
                <p class="row-title">{{ item.building }} {{ item.roomNo }}</p>
                <p class="row-sub">{{ text.hot.capacity }} {{ item.capacity || '-' }} ? {{ item.location || text.hot.locationEmpty }}</p>
              </div>
              <span class="row-tag">{{ item.reservationCount || 0 }} {{ text.hot.times }}</span>
            </div>
          </div>
          <div v-else class="empty">{{ text.hot.empty }}</div>
          <div class="panel-footer">
            <div>
              <p class="row-title">{{ text.hot.approvedTitle }}</p>
              <p class="row-sub">{{ text.hot.approvedDesc }} {{ overview?.reservationApprovedCount || 0 }} {{ text.hot.approvedUnit }}</p>
            </div>
            <span class="status-pill">{{ text.hot.status }}</span>
          </div>
        </div>
      </section>

      <section class="container section">
        <div class="section-title">
          <h2>{{ text.services.title }}</h2>
          <span>{{ text.services.sub }}</span>
        </div>
        <div class="service-grid">
          <RouterLink v-for="service in services" :key="service.title" :to="service.to" class="service-card card">
            <div class="service-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path :d="service.icon" fill="currentColor" />
              </svg>
            </div>
            <div>
              <div class="service-header">
                <h3>{{ service.title }}</h3>
                <span class="service-tag">{{ service.tag }}</span>
              </div>
              <p class="subtle">{{ service.desc }}</p>
            </div>
          </RouterLink>
        </div>
      </section>

      <section class="container section split">
        <div class="card lecture-card">
          <div class="section-title">
            <h2>{{ text.lectures.title }}</h2>
            <span>{{ text.lectures.sub }}</span>
          </div>
          <div v-if="upcomingLectures.length" class="lecture-list">
            <div v-for="lecture in upcomingLectures" :key="lecture.id" class="lecture-row">
              <div>
                <p class="row-title">{{ lecture.title }}</p>
                <p class="row-sub">{{ lecture.location || text.lectures.locationEmpty }} ? {{ formatTime(lecture.startTime) }}</p>
              </div>
              <span class="lecture-tag">{{ text.lectures.capacity }} {{ lecture.capacity }}</span>
            </div>
          </div>
          <div v-else class="empty">{{ text.lectures.empty }}</div>
          <RouterLink class="btn ghost full" to="/lectures">{{ text.lectures.viewAll }}</RouterLink>
        </div>

        <div class="card highlight-card">
          <div class="section-title">
            <h2>{{ text.highlights.title }}</h2>
            <span>{{ text.highlights.sub }}</span>
          </div>
          <div class="highlight-grid">
            <div v-for="highlight in highlights" :key="highlight.title" class="highlight-item">
              <h4>{{ highlight.title }}</h4>
              <p class="subtle">{{ highlight.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="container section">
        <div class="card notice-card">
          <div class="section-title">
            <h2>{{ text.notices.title }}</h2>
            <span>{{ text.notices.sub }}</span>
          </div>
          <div v-if="!loggedIn" class="empty">
            {{ text.notices.login }}
            <RouterLink class="btn ghost full" to="/login">{{ text.notices.goLogin }}</RouterLink>
          </div>
          <div v-else>
            <p v-if="messageError" class="error">{{ messageError }}</p>
            <div v-if="messages.length" class="notice-list">
              <div v-for="item in messages" :key="item.id" class="notice-row">
                <div>
                  <p class="row-title">{{ item.title }}</p>
                  <p class="row-sub">{{ item.content }}</p>
                </div>
                <span class="notice-time">{{ formatTime(item.createdAt) }}</span>
              </div>
            </div>
            <div v-else class="empty">{{ text.notices.empty }}</div>
            <RouterLink class="btn ghost full" to="/messages">{{ text.notices.view }}</RouterLink>
          </div>
        </div>
      </section>

      <div v-if="error" class="container error-banner">{{ error }}</div>
    </div>
  </MarketingLayout>
</template>

<style scoped>
.page {
  padding: 36px 0 80px;
}

.hero {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 32px;
  align-items: center;
  margin-top: 16px;
}

.hero-copy h1 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 40px;
  line-height: 1.2;
  margin: 16px 0 12px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 28px;
}

.metric-value {
  font-size: 20px;
  font-weight: 600;
}

.metric-label {
  font-size: 13px;
  color: var(--text-muted);
}

.hero-badges {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.hero-panel {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.panel-header h3 {
  font-size: 18px;
  margin-bottom: 4px;
}

.panel-list {
  display: grid;
  gap: 12px;
}

.panel-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #f8fafc;
  border-radius: var(--radius-md);
}

.row-title {
  font-weight: 600;
}

.row-sub {
  font-size: 13px;
  color: var(--text-muted);
}

.row-tag {
  padding: 4px 10px;
  background: rgba(29, 78, 216, 0.12);
  color: var(--primary);
  font-size: 12px;
  border-radius: 999px;
}

.panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-top: 1px dashed var(--border);
  padding-top: 14px;
}

.status-pill {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(14, 165, 233, 0.16);
  color: #0b72a6;
  font-size: 12px;
  font-weight: 600;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.service-card {
  padding: 18px;
  display: grid;
  gap: 12px;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.service-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.service-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: rgba(29, 78, 216, 0.12);
  color: var(--primary);
  display: grid;
  place-items: center;
}

.service-icon svg {
  width: 22px;
  height: 22px;
}

.service-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 6px;
}

.service-card h3 {
  font-size: 18px;
}

.service-tag {
  font-size: 12px;
  color: var(--text-muted);
  padding: 4px 10px;
  border-radius: 999px;
  background: #f1f5f9;
}

.split {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 20px;
}

.lecture-card,
.highlight-card,
.notice-card {
  padding: 20px;
}

.lecture-list,
.notice-list {
  display: grid;
  gap: 16px;
}

.lecture-row,
.notice-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
}

.lecture-row:last-child,
.notice-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.lecture-tag {
  font-size: 12px;
  color: var(--primary);
  background: rgba(29, 78, 216, 0.12);
  padding: 4px 10px;
  border-radius: 999px;
}

.full {
  width: 100%;
  margin-top: 12px;
}

.highlight-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.highlight-item h4 {
  font-size: 16px;
  margin-bottom: 6px;
}

.notice-time {
  font-size: 12px;
  color: var(--text-light);
}

.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}

.empty {
  padding: 16px;
  border: 1px dashed var(--border);
  border-radius: var(--radius-md);
  color: var(--text-light);
  text-align: center;
}

.error {
  color: #dc2626;
  font-size: 13px;
}

.error-banner {
  margin-top: 16px;
  color: #dc2626;
}

@media (max-width: 1024px) {
  .hero,
  .split {
    grid-template-columns: 1fr;
  }

  .hero-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-copy h1 {
    font-size: 30px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .highlight-grid {
    grid-template-columns: 1fr;
  }
}
</style>
