<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'
import { isAdmin } from '@/services/auth'

const text = {
  title: '服务工作台',
  error: '加载失败',
  refresh: '刷新数据',
  refreshing: '刷新中...',
  summary: {
    classroom: '教室资源',
    equipment: '设备数量',
    bus: '校车班次',
    reservation: '预约总量',
    pending: '待审批',
    unread: '未读消息'
  },
  summaryDesc: {
    classroom: '实时资源数量',
    equipment: '资产与资源池',
    bus: '当期可配置班次',
    reservation: '含审批中与已通过',
    pending: '需要管理员处理',
    unread: '站内消息中心'
  },
  charts: {
    reservation: '我的预约状态',
    reservationSub: '统计我的预约状态分布',
    activity: '我的业务分布',
    activitySub: '进行中 / 已完成 / 已取消',
    admin: '全局业务总量',
    adminSub: '系统各模块累计数量'
  },
  status: {
    reservationPending: '待审批',
    reservationApproved: '已批准',
    reservationRejected: '已拒绝',
    reservationCanceled: '已取消',
    pending: '进行中',
    done: '已完成',
    canceled: '已取消'
  },
  modules: {
    reservation: '预约',
    equipment: '设备借用',
    lecture: '讲座报名',
    bus: '校车预约'
  },
  recent: {
    title: '我的最近预约',
    sub: '按开始时间排序',
    empty: '暂无预约'
  },
  messages: {
    title: '我的最新消息',
    sub: '站内消息中心',
    empty: '暂无消息',
    view: '进入消息中心'
  }
}

const admin = isAdmin()

const summary = ref({
  classroomCount: 0,
  reservationCount: 0,
  pendingApprovalCount: 0,
  unreadMessageCount: 0,
  equipmentCount: 0,
  busTripCount: 0
})
const loading = ref(false)
const error = ref('')

const reservations = ref([])
const borrows = ref([])
const lectures = ref([])
const busBookings = ref([])
const messages = ref([])
const adminOverview = ref(null)

const reservationChartRef = ref(null)
const activityChartRef = ref(null)
const adminChartRef = ref(null)
let reservationChart
let activityChart
let adminChart

const countByStatus = (list) => {
  const map = {}
  list.forEach((item) => {
    const status = item.status || 'UNKNOWN'
    map[status] = (map[status] || 0) + 1
  })
  return map
}

const getCount = (map, key) => (map[key] || 0)

const buildReservationSeries = () => {
  const stats = countByStatus(reservations.value)
  return [
    { value: getCount(stats, 'PENDING_APPROVAL'), name: text.status.reservationPending },
    { value: getCount(stats, 'APPROVED'), name: text.status.reservationApproved },
    { value: getCount(stats, 'REJECTED'), name: text.status.reservationRejected },
    { value: getCount(stats, 'CANCELED'), name: text.status.reservationCanceled }
  ]
}

const buildActivitySeries = () => {
  const reservationStats = countByStatus(reservations.value)
  const equipmentStats = countByStatus(borrows.value)
  const lectureStats = countByStatus(lectures.value)
  const busStats = countByStatus(busBookings.value)

  const inProgress = [
    getCount(reservationStats, 'PENDING_APPROVAL'),
    getCount(equipmentStats, 'PENDING') + getCount(equipmentStats, 'APPROVED'),
    getCount(lectureStats, 'SIGNED_UP') + getCount(lectureStats, 'WAITLIST'),
    getCount(busStats, 'WAITLIST')
  ]

  const done = [
    getCount(reservationStats, 'APPROVED'),
    getCount(equipmentStats, 'RETURNED'),
    getCount(lectureStats, 'CHECKED_IN'),
    getCount(busStats, 'BOOKED')
  ]

  const canceled = [
    getCount(reservationStats, 'REJECTED') + getCount(reservationStats, 'CANCELED'),
    getCount(equipmentStats, 'REJECTED') + getCount(equipmentStats, 'CANCELED'),
    getCount(lectureStats, 'CANCELED'),
    getCount(busStats, 'CANCELED')
  ]

  return { inProgress, done, canceled }
}

const buildAdminSeries = () => {
  const data = adminOverview.value || {}
  return [
    data.reservationTotal || 0,
    data.equipmentBorrowTotal || 0,
    data.lectureSignupTotal || 0,
    data.busBookingTotal || 0
  ]
}

const initCharts = () => {
  if (reservationChartRef.value && !reservationChart) {
    reservationChart = echarts.init(reservationChartRef.value)
  }
  if (activityChartRef.value && !activityChart) {
    activityChart = echarts.init(activityChartRef.value)
  }
  if (adminChartRef.value && !adminChart) {
    adminChart = echarts.init(adminChartRef.value)
  }
}

const updateCharts = () => {
  if (reservationChart) {
    reservationChart.setOption(
      {
        tooltip: { trigger: 'item' },
        legend: { bottom: 0, textStyle: { color: '#475569', fontSize: 12 } },
        series: [
          {
            name: text.charts.reservation,
            type: 'pie',
            radius: ['45%', '70%'],
            avoidLabelOverlap: true,
            label: { formatter: '{b}: {c}' },
            data: buildReservationSeries()
          }
        ]
      },
      true
    )
  }

  if (activityChart) {
    const activity = buildActivitySeries()
    activityChart.setOption(
      {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { top: 0, textStyle: { color: '#475569', fontSize: 12 } },
        grid: { left: 10, right: 10, bottom: 20, containLabel: true },
        xAxis: { type: 'category', data: [text.modules.reservation, text.modules.equipment, text.modules.lecture, text.modules.bus] },
        yAxis: { type: 'value' },
        series: [
          { name: text.status.pending, type: 'bar', stack: 'total', data: activity.inProgress, itemStyle: { color: '#38bdf8' } },
          { name: text.status.done, type: 'bar', stack: 'total', data: activity.done, itemStyle: { color: '#22c55e' } },
          { name: text.status.canceled, type: 'bar', stack: 'total', data: activity.canceled, itemStyle: { color: '#f97316' } }
        ]
      },
      true
    )
  }

  if (adminChart) {
    adminChart.setOption(
      {
        tooltip: { trigger: 'axis' },
        grid: { left: 10, right: 10, bottom: 20, containLabel: true },
        xAxis: { type: 'category', data: [text.modules.reservation, text.modules.equipment, text.modules.lecture, text.modules.bus] },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'bar',
            data: buildAdminSeries(),
            itemStyle: { color: '#6366f1' },
            barWidth: 36
          }
        ]
      },
      true
    )
  }
}

const renderCharts = async () => {
  await nextTick()
  initCharts()
  updateCharts()
}

const loadAll = async () => {
  loading.value = true
  error.value = ''
  try {
    const tasks = [
      request('/api/dashboard/summary'),
      request('/api/reservations/my'),
      request('/api/equipments/borrows/my'),
      request('/api/lectures/my/signups'),
      request('/api/bus/bookings/my'),
      request('/api/messages')
    ]
    if (admin) {
      tasks.push(request('/api/admin/reports/overview'))
    }
    const results = await Promise.all(tasks)
    summary.value = results[0]
    reservations.value = results[1] || []
    borrows.value = results[2] || []
    lectures.value = results[3] || []
    busBookings.value = results[4] || []
    messages.value = (results[5] || []).slice(0, 4)
    if (admin) {
      adminOverview.value = results[6]
    }
  } catch (e) {
    error.value = e.message || text.error
  } finally {
    loading.value = false
    await renderCharts()
  }
}

const recentReservations = computed(() => reservations.value.slice(0, 4))

const reservationStatusLabel = (status) => {
  switch (status) {
    case 'PENDING_APPROVAL':
      return text.status.reservationPending
    case 'APPROVED':
      return text.status.reservationApproved
    case 'REJECTED':
      return text.status.reservationRejected
    case 'CANCELED':
      return text.status.reservationCanceled
    default:
      return status || '-'
  }
}

const formatTime = (value) => {
  if (!value) return '-'
  return value.replace('T', ' ')
}

const resizeCharts = () => {
  if (reservationChart) reservationChart.resize()
  if (activityChart) activityChart.resize()
  if (adminChart) adminChart.resize()
}

onMounted(async () => {
  await loadAll()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  if (reservationChart) reservationChart.dispose()
  if (activityChart) activityChart.dispose()
  if (adminChart) adminChart.dispose()
})
</script>

<template>
  <AppShell :title="text.title">
    <div v-if="error" class="card error-card">{{ error }}</div>

    <div class="summary-grid">
      <div class="card summary-card">
        <p class="summary-label">{{ text.summary.classroom }}</p>
        <h2>{{ summary.classroomCount }}</h2>
        <span class="subtle">{{ text.summaryDesc.classroom }}</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">{{ text.summary.equipment }}</p>
        <h2>{{ summary.equipmentCount }}</h2>
        <span class="subtle">{{ text.summaryDesc.equipment }}</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">{{ text.summary.bus }}</p>
        <h2>{{ summary.busTripCount }}</h2>
        <span class="subtle">{{ text.summaryDesc.bus }}</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">{{ text.summary.reservation }}</p>
        <h2>{{ summary.reservationCount }}</h2>
        <span class="subtle">{{ text.summaryDesc.reservation }}</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">{{ text.summary.pending }}</p>
        <h2>{{ summary.pendingApprovalCount }}</h2>
        <span class="subtle">{{ text.summaryDesc.pending }}</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">{{ text.summary.unread }}</p>
        <h2>{{ summary.unreadMessageCount }}</h2>
        <span class="subtle">{{ text.summaryDesc.unread }}</span>
      </div>
    </div>

    <div class="chart-grid">
      <div class="card chart-card">
        <div class="section-title">
          <h2>{{ text.charts.reservation }}</h2>
          <span>{{ text.charts.reservationSub }}</span>
        </div>
        <div ref="reservationChartRef" class="chart"></div>
      </div>

      <div class="card chart-card">
        <div class="section-title">
          <h2>{{ text.charts.activity }}</h2>
          <span>{{ text.charts.activitySub }}</span>
        </div>
        <div ref="activityChartRef" class="chart"></div>
      </div>

      <div v-if="admin" class="card chart-card">
        <div class="section-title">
          <h2>{{ text.charts.admin }}</h2>
          <span>{{ text.charts.adminSub }}</span>
        </div>
        <div ref="adminChartRef" class="chart"></div>
      </div>
    </div>

    <div class="list-grid">
      <div class="card list-card">
        <div class="section-title">
          <h2>{{ text.recent.title }}</h2>
          <span>{{ text.recent.sub }}</span>
        </div>
        <div v-if="recentReservations.length" class="list-body">
          <div v-for="item in recentReservations" :key="item.id" class="list-row">
            <div>
              <p class="row-title">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</p>
              <p class="row-sub">{{ reservationStatusLabel(item.status) }}</p>
            </div>
            <RouterLink class="btn ghost small" :to="`/reservations/my`">{{ text.modules.reservation }}</RouterLink>
          </div>
        </div>
        <div v-else class="empty">{{ text.recent.empty }}</div>
      </div>

      <div class="card list-card">
        <div class="section-title">
          <h2>{{ text.messages.title }}</h2>
          <span>{{ text.messages.sub }}</span>
        </div>
        <div v-if="messages.length" class="list-body">
          <div v-for="item in messages" :key="item.id" class="list-row">
            <div>
              <p class="row-title">{{ item.title }}</p>
              <p class="row-sub">{{ item.content }}</p>
            </div>
            <span class="notice-time">{{ formatTime(item.createdAt) }}</span>
          </div>
          <RouterLink class="btn ghost full" to="/messages">{{ text.messages.view }}</RouterLink>
        </div>
        <div v-else class="empty">{{ text.messages.empty }}</div>
      </div>
    </div>

    <button class="btn" @click="loadAll" :disabled="loading">
      {{ loading ? text.refreshing : text.refresh }}
    </button>
  </AppShell>
</template>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
}

.summary-card {
  padding: 18px;
  display: grid;
  gap: 6px;
}

.summary-card h2 {
  font-size: 28px;
}

.summary-label {
  font-size: 13px;
  color: var(--text-muted);
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.chart-card {
  padding: 20px;
}

.chart {
  width: 100%;
  height: 280px;
}

.list-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.list-card {
  padding: 20px;
  display: grid;
  gap: 12px;
}

.list-body {
  display: grid;
  gap: 12px;
}

.list-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.list-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.notice-time {
  font-size: 12px;
  color: var(--text-light);
}

.error-card {
  padding: 16px;
  color: #dc2626;
}

.empty {
  padding: 16px;
  border: 1px dashed var(--border);
  border-radius: var(--radius-md);
  color: var(--text-light);
  text-align: center;
}

.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}

.full {
  width: 100%;
}

@media (max-width: 1280px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }

  .list-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
