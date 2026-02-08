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
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.startTime }} - {{ item.endTime }}</td>
            <td>{{ item.classroomId }}</td>
            <td><span class="tag">{{ statusText(item.status) }}</span></td>
            <td>{{ item.reason || '-' }}</td>
            <td>
              <button class="btn ghost" @click="cancel(item.id)" :disabled="item.status === 'CANCELED'">
                取消
              </button>
            </td>
          </tr>
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

.error-card {
  padding: 16px;
  color: #dc2626;
}
</style>
