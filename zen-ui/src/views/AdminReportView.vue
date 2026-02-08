<script setup>
import { computed, onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const loading = ref(false)
const error = ref('')
const overview = ref(null)

const text = {
  title: '运营报表',
  heroLabel: '实时统计',
  heroTitle: '服务中心已经汇总当前业务数据。',
  heroSub: '包括预约、设备、讲座、校车等模块，数据实时更新。',
  refresh: '刷新数据',
  loading: '加载中',
  loadFailed: '加载失败',
  reservation: '教室预约',
  equipment: '设备借用',
  lecture: '讲座活动',
  bus: '校车预约',
  approved: '已完成审批',
  approvedShort: '已审批',
  signup: '报名',
  booked: '已定',
  waitlist: '等待',
  trendTitle: '转化与签到',
  trendSub: '标记审批通过、等待状态与签到趋势',
  hintTitle: '运营提示',
  hintSub: '根据现有数据自动生成',
  reservationApproveRate: '预约审批通过率',
  equipmentApproveRate: '设备审批通过率',
  lectureCheckinRate: '讲座签到率',
  busBookedRate: '校车已定率',
  approvalBacklog: '审批待办',
  approvalBacklogHint: '请尽快处理新申请',
  approvalClearHint: '目前没有待办',
  busWaitlist: '校车等待',
  busWaitlistHint: '可以通过增加班次缓解压力',
  busWaitlistClear: '等待用户几乎为零',
  lectureCheckin: '讲座签到',
  lectureCheckinHint: '特别提醒启用前一小时签到'
}

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    overview.value = await request('/api/admin/reports/overview')
  } catch (e) {
    error.value = e.message || text.loadFailed
  } finally {
    loading.value = false
  }
}

const format = (value) => new Intl.NumberFormat('zh-CN').format(value || 0)
const percent = (part, total) => (total ? Math.round((part / total) * 100) : 0)

const summaryCards = computed(() => {
  const data = overview.value || {}
  return [
    {
      title: text.reservation,
      value: data.reservationTotal || 0,
      meta: `${data.reservationApproved || 0} ${text.approved}`,
      tone: 'tone-blue'
    },
    {
      title: text.equipment,
      value: data.equipmentBorrowTotal || 0,
      meta: `${data.equipmentBorrowApproved || 0} ${text.approvedShort}`,
      tone: 'tone-emerald'
    },
    {
      title: text.lecture,
      value: data.lectureTotal || 0,
      meta: `${data.lectureSignupTotal || 0} ${text.signup}`,
      tone: 'tone-purple'
    },
    {
      title: text.bus,
      value: data.busBookingTotal || 0,
      meta: `${data.busBooked || 0} ${text.booked} / ${data.busWaitlist || 0} ${text.waitlist}`,
      tone: 'tone-amber'
    }
  ]
})

const progressRows = computed(() => {
  const data = overview.value || {}
  return [
    {
      label: text.reservationApproveRate,
      value: percent(data.reservationApproved, data.reservationTotal),
      current: data.reservationApproved || 0,
      total: data.reservationTotal || 0,
      accent: 'bar-blue'
    },
    {
      label: text.equipmentApproveRate,
      value: percent(data.equipmentBorrowApproved, data.equipmentBorrowTotal),
      current: data.equipmentBorrowApproved || 0,
      total: data.equipmentBorrowTotal || 0,
      accent: 'bar-emerald'
    },
    {
      label: text.lectureCheckinRate,
      value: percent(data.lectureCheckinTotal, data.lectureSignupTotal),
      current: data.lectureCheckinTotal || 0,
      total: data.lectureSignupTotal || 0,
      accent: 'bar-purple'
    },
    {
      label: text.busBookedRate,
      value: percent(data.busBooked, data.busBookingTotal),
      current: data.busBooked || 0,
      total: data.busBookingTotal || 0,
      accent: 'bar-amber'
    }
  ]
})

const insights = computed(() => {
  const data = overview.value || {}
  const pendingApprovals = (data.reservationPending || 0) + (data.equipmentBorrowPending || 0)
  const waitlist = data.busWaitlist || 0
  const checkinRate = percent(data.lectureCheckinTotal, data.lectureSignupTotal)
  return [
    {
      title: text.approvalBacklog,
      value: pendingApprovals,
      desc: pendingApprovals ? text.approvalBacklogHint : text.approvalClearHint
    },
    {
      title: text.busWaitlist,
      value: waitlist,
      desc: waitlist ? text.busWaitlistHint : text.busWaitlistClear
    },
    {
      title: text.lectureCheckin,
      value: `${checkinRate}%`,
      desc: text.lectureCheckinHint
    }
  ]
})

onMounted(load)
</script>

<template>
  <AppShell :title="text.title">
    <div class="card hero-card">
      <div>
        <p class="hero-label">{{ text.heroLabel }}</p>
        <h2>{{ text.heroTitle }}</h2>
        <p class="hero-sub">{{ text.heroSub }}</p>
      </div>
      <div class="hero-actions">
        <button class="btn ghost" type="button" @click="load">{{ text.refresh }}</button>
        <span v-if="loading" class="pill">{{ text.loading }}</span>
      </div>
    </div>

    <p v-if="error" class="alert error">{{ error }}</p>

    <div class="stats-grid">
      <div v-for="item in summaryCards" :key="item.title" class="card stat-card" :class="item.tone">
        <p class="stat-title">{{ item.title }}</p>
        <p class="stat-value">{{ format(item.value) }}</p>
        <p class="stat-meta">{{ item.meta }}</p>
      </div>
    </div>

    <div class="split-grid">
      <div class="card progress-card">
        <div class="section-title">
          <h2>{{ text.trendTitle }}</h2>
          <span>{{ text.trendSub }}</span>
        </div>
        <div class="progress-list">
          <div v-for="item in progressRows" :key="item.label" class="progress-item">
            <div class="progress-head">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}%</strong>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :class="item.accent" :style="{ width: `${item.value}%` }"></div>
            </div>
            <p class="progress-meta">{{ format(item.current) }} / {{ format(item.total) }}</p>
          </div>
        </div>
      </div>

      <div class="card insight-card">
        <div class="section-title">
          <h2>{{ text.hintTitle }}</h2>
          <span>{{ text.hintSub }}</span>
        </div>
        <div class="insight-list">
          <div v-for="item in insights" :key="item.title" class="insight-item">
            <div>
              <p class="insight-title">{{ item.title }}</p>
              <p class="insight-desc">{{ item.desc }}</p>
            </div>
            <span class="insight-value">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.hero-card {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.08), rgba(14, 165, 233, 0.08));
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.hero-card h2 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 22px;
  margin-bottom: 8px;
}

.hero-label {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--text-light);
  margin-bottom: 8px;
}

.hero-sub {
  color: var(--text-muted);
  font-size: 14px;
  max-width: 540px;
}

.hero-actions {
  display: grid;
  align-content: flex-start;
  gap: 10px;
}

.alert {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13px;
}

.alert.error {
  background: rgba(220, 38, 38, 0.08);
  color: #dc2626;
  border: 1px solid rgba(220, 38, 38, 0.2);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.stat-card {
  padding: 18px 20px;
  display: grid;
  gap: 6px;
  border: 1px solid transparent;
}

.stat-title {
  color: var(--text-muted);
  font-size: 13px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
}

.stat-meta {
  font-size: 12px;
  color: var(--text-light);
}

.tone-blue {
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.1), #ffffff);
  border-color: rgba(29, 78, 216, 0.18);
}

.tone-emerald {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.12), #ffffff);
  border-color: rgba(16, 185, 129, 0.2);
}

.tone-purple {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.12), #ffffff);
  border-color: rgba(99, 102, 241, 0.2);
}

.tone-amber {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.12), #ffffff);
  border-color: rgba(245, 158, 11, 0.2);
}

.split-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
  gap: 16px;
}

.progress-card,
.insight-card {
  padding: 20px;
}

.progress-list {
  display: grid;
  gap: 16px;
}

.progress-head {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 6px;
}

.progress-bar {
  height: 10px;
  background: #e2e8f0;
  border-radius: 999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 999px;
  transition: width 0.2s ease;
}

.bar-blue {
  background: linear-gradient(90deg, #1d4ed8, #38bdf8);
}

.bar-emerald {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.bar-purple {
  background: linear-gradient(90deg, #6366f1, #a5b4fc);
}

.bar-amber {
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
}

.progress-meta {
  font-size: 12px;
  color: var(--text-light);
  margin-top: 6px;
}

.insight-list {
  display: grid;
  gap: 12px;
}

.insight-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.04);
}

.insight-title {
  font-size: 14px;
  font-weight: 600;
}

.insight-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.insight-value {
  font-weight: 600;
  color: var(--primary);
}

@media (max-width: 1100px) {
  .hero-card {
    flex-direction: column;
  }

  .split-grid {
    grid-template-columns: 1fr;
  }
}
</style>
