<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const loading = ref(false)
const error = ref('')
const timelineMap = ref({})
const timelineLoading = ref({})

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    list.value = await request('/api/reservations/my')
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const cancel = async (id) => {
  if (!confirm('确认取消该预约？')) return
  try {
    await request(`/api/reservations/${id}/cancel`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '取消失败')
  }
}

const toggleTimeline = async (id) => {
  if (timelineMap.value[id]) {
    timelineMap.value = { ...timelineMap.value, [id]: null }
    return
  }
  timelineLoading.value = { ...timelineLoading.value, [id]: true }
  try {
    const data = await request(`/api/reservations/${id}/timeline`)
    timelineMap.value = { ...timelineMap.value, [id]: data }
  } catch (e) {
    alert(e.message || '加载失败')
  } finally {
    timelineLoading.value = { ...timelineLoading.value, [id]: false }
  }
}

const statusText = (status) => {
  switch (status) {
    case 'PENDING_APPROVAL':
      return '审批中'
    case 'APPROVED':
      return '已通过'
    case 'REJECTED':
      return '已驳回'
    case 'CANCELED':
      return '已取消'
    default:
      return status
  }
}

const timelineStatus = (status) => {
  switch (status) {
    case 'DONE':
      return '已完成'
    case 'PENDING':
      return '处理中'
    case 'REJECTED':
      return '已驳回'
    case 'CANCELED':
      return '已取消'
    default:
      return status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="我的预约">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>预约时间</th>
            <th>教室ID</th>
            <th>状态</th>
            <th>说明</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <template v-for="item in list" :key="item.id">
            <tr>
              <td>{{ item.startTime }} - {{ item.endTime }}</td>
              <td>{{ item.classroomId }}</td>
              <td><span class="tag">{{ statusText(item.status) }}</span></td>
              <td>{{ item.reason || '-' }}</td>
              <td class="actions">
                <button class="btn ghost" @click="cancel(item.id)" :disabled="item.status === 'CANCELED'">
                  取消
                </button>
                <button class="btn" @click="toggleTimeline(item.id)">
                  {{ timelineMap[item.id] ? '收起流程' : '查看流程' }}
                </button>
              </td>
            </tr>
            <tr v-if="timelineMap[item.id]">
              <td colspan="5">
                <div class="timeline">
                  <div v-for="(step, index) in timelineMap[item.id]" :key="index" class="timeline-item">
                    <div class="dot"></div>
                    <div>
                      <p class="timeline-title">
                        {{ step.name }} · {{ timelineStatus(step.status) }}
                      </p>
                      <p class="subtle" v-if="step.time">{{ step.time }}</p>
                      <p class="subtle" v-if="step.remark">备注：{{ step.remark }}</p>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            <tr v-if="timelineLoading[item.id]">
              <td colspan="5" class="subtle">加载流程中...</td>
            </tr>
          </template>
        </tbody>
      </table>
      <div v-if="!list.length && !loading" class="empty">暂无预约记录</div>
    </div>
  </AppShell>
</template>

<style scoped>
.table-card {
  padding: 10px 16px 18px;
}

.actions {
  display: flex;
  gap: 8px;
}

.timeline {
  display: grid;
  gap: 12px;
  padding: 12px 0;
}

.timeline-item {
  display: grid;
  grid-template-columns: 18px 1fr;
  gap: 12px;
  align-items: start;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--accent);
  margin-top: 6px;
}

.timeline-title {
  font-weight: 600;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}
</style>
