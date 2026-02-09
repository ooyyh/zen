<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/study-rooms/reservations/my')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const cancel = async (id) => {
  if (!confirm('确认取消该座位预约？')) return
  try {
    await request(`/api/study-rooms/reservations/${id}/cancel`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '取消失败')
  }
}

const checkIn = async (id) => {
  if (!confirm('确认签到？')) return
  try {
    await request(`/api/study-rooms/reservations/${id}/check-in`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '签到失败')
  }
}

const statusText = (status) => {
  switch (status) {
    case 'RESERVED':
      return '已预约'
    case 'CHECKED_IN':
      return '已签到'
    case 'COMPLETED':
      return '已完成'
    case 'CANCELED':
      return '已取消'
    case 'EXPIRED':
      return '已过期'
    default:
      return status
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(load)
</script>

<template>
  <AppShell title="我的座位">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>自习室</th>
            <th>座位</th>
            <th>时间</th>
            <th>状态</th>
            <th>签到时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>
              <div class="room-info">
                <div>{{ item.studyRoomName }}</div>
                <div class="room-location">{{ item.building }} {{ item.floor }}F</div>
              </div>
            </td>
            <td><strong>{{ item.seatNo }}</strong></td>
            <td>
              <div class="time-range">
                <div>{{ formatTime(item.startTime) }}</div>
                <div>{{ formatTime(item.endTime) }}</div>
              </div>
            </td>
            <td><span class="tag">{{ statusText(item.status) }}</span></td>
            <td>{{ item.checkInAt ? formatTime(item.checkInAt) : '-' }}</td>
            <td class="actions">
              <button v-if="item.status === 'RESERVED'" class="btn primary" @click="checkIn(item.id)">
                签到
              </button>
              <button v-if="item.status === 'RESERVED'" class="btn ghost" @click="cancel(item.id)">
                取消
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无预约记录</div>
    </div>
  </AppShell>
</template>

<style scoped>
.table-card {
  padding: 10px 16px 18px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}

.room-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.room-location {
  font-size: 12px;
  color: #6b7280;
}

@media (prefers-color-scheme: dark) {
  .room-location {
    color: #9ca3af;
  }
}

.time-range {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
}

.actions {
  display: flex;
  gap: 8px;
}
</style>
