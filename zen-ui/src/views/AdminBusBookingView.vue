<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')
const statusFilter = ref('')

const load = async () => {
  error.value = ''
  try {
    const params = new URLSearchParams()
    if (statusFilter.value) params.append('status', statusFilter.value)
    list.value = await request(`/api/admin/bus/bookings?${params.toString()}`)
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="校车预约记录">
    <div class="card filter-card">
      <div class="filter-grid">
        <label class="field">
          状态筛选
          <select v-model="statusFilter" class="select">
            <option value="">全部</option>
            <option value="BOOKED">已预约</option>
            <option value="WAITLIST">候补</option>
            <option value="CANCELED">已取消</option>
          </select>
        </label>
        <div class="filter-actions">
          <button class="btn primary" @click="load">筛选</button>
        </div>
      </div>
    </div>

    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>用户</th>
            <th>线路</th>
            <th>时间</th>
            <th>车次</th>
            <th>状态</th>
            <th>预约时间</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.username }}</td>
            <td>{{ item.origin }} → {{ item.destination }}</td>
            <td>{{ item.departureTime }} - {{ item.arrivalTime }}</td>
            <td>{{ item.busNo || '-' }}</td>
            <td><span class="tag">{{ item.status }}</span></td>
            <td>{{ item.createdAt }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无预约记录</div>
    </div>
  </AppShell>
</template>

<style scoped>
.filter-card {
  padding: 16px;
}

.filter-grid {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 16px;
  align-items: end;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.table-card {
  padding: 10px 16px 18px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}
</style>
