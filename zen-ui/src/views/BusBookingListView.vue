<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/bus/bookings/my')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const cancel = async (id) => {
  if (!confirm('确认取消预约？')) return
  try {
    await request(`/api/bus/bookings/${id}/cancel`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '取消失败')
  }
}

const statusText = (status) => {
  switch (status) {
    case 'BOOKED':
      return '已预约'
    case 'WAITLIST':
      return '候补中'
    case 'CANCELED':
      return '已取消'
    default:
      return status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="我的校车">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>线路</th>
            <th>时间</th>
            <th>车次</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.origin }} → {{ item.destination }}</td>
            <td>{{ item.departureTime }} - {{ item.arrivalTime }}</td>
            <td>{{ item.busNo || '-' }}</td>
            <td><span class="tag">{{ statusText(item.status) }}</span></td>
            <td>
              <button class="btn ghost" @click="cancel(item.id)" :disabled="item.status === 'CANCELED'">
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
</style>
