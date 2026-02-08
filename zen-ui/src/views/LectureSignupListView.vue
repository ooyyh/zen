<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const loading = ref(false)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    list.value = await request('/api/lectures/my/signups')
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const cancel = async (lectureId) => {
  if (!confirm('确认取消报名？')) return
  try {
    await request(`/api/lectures/${lectureId}/cancel`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '取消失败')
  }
}

const checkin = async (lectureId) => {
  try {
    await request(`/api/lectures/${lectureId}/checkin`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '签到失败')
  }
}

const statusText = (status) => {
  switch (status) {
    case 'SIGNED_UP':
      return '已报名'
    case 'WAITLIST':
      return '候补中'
    case 'CANCELED':
      return '已取消'
    case 'CHECKED_IN':
      return '已签到'
    default:
      return status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="我的讲座">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>讲座</th>
            <th>时间</th>
            <th>地点</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.title }}</td>
            <td>{{ item.startTime }} - {{ item.endTime }}</td>
            <td>{{ item.location || '-' }}</td>
            <td><span class="tag">{{ statusText(item.status) }}</span></td>
            <td class="actions">
              <button
                class="btn ghost"
                @click="cancel(item.lectureId)"
                :disabled="item.status !== 'SIGNED_UP'"
              >
                取消
              </button>
              <button
                class="btn primary"
                @click="checkin(item.lectureId)"
                :disabled="item.status !== 'SIGNED_UP'"
              >
                签到
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length && !loading" class="empty">暂无报名记录</div>
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

.error-card {
  padding: 16px;
  color: #dc2626;
}
</style>
