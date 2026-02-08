<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const summary = ref({
  classroomCount: 0,
  reservationCount: 0,
  pendingApprovalCount: 0,
  unreadMessageCount: 0
})
const loading = ref(true)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    summary.value = await request('/api/dashboard/summary')
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="服务工作台">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div v-else class="summary-grid">
      <div class="card summary-card">
        <p class="summary-label">可用教室</p>
        <h2>{{ summary.classroomCount }}</h2>
        <span class="subtle">实时资源数量</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">预约总量</p>
        <h2>{{ summary.reservationCount }}</h2>
        <span class="subtle">含审批中与已通过</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">待审批</p>
        <h2>{{ summary.pendingApprovalCount }}</h2>
        <span class="subtle">需要管理员处理</span>
      </div>
      <div class="card summary-card">
        <p class="summary-label">未读消息</p>
        <h2>{{ summary.unreadMessageCount }}</h2>
        <span class="subtle">站内消息中心</span>
      </div>
    </div>

    <div class="card notice-card">
      <div class="section-title">
        <h2>操作指引</h2>
        <span>系统基础流程</span>
      </div>
      <div class="guide-grid">
        <div>
          <h4>学生/教师</h4>
          <p class="subtle">进入“发起预约”选择教室与时间，提交后等待审批。</p>
        </div>
        <div>
          <h4>管理员</h4>
          <p class="subtle">在“审批处理”中批量处理申请，并可调整预约规则。</p>
        </div>
        <div>
          <h4>通知中心</h4>
          <p class="subtle">系统自动推送进度提醒，避免漏处理。</p>
        </div>
      </div>
    </div>

    <button class="btn" @click="load" :disabled="loading">
      {{ loading ? '刷新中...' : '刷新数据' }}
    </button>
  </AppShell>
</template>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
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

.notice-card {
  padding: 20px;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}

@media (max-width: 1024px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .guide-grid {
    grid-template-columns: 1fr;
  }
}
</style>
