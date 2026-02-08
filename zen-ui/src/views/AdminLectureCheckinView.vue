<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const lectures = ref([])
const selectedId = ref('')
const list = ref([])
const error = ref('')

const loadLectures = async () => {
  try {
    lectures.value = await request('/api/admin/lectures')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const loadCheckins = async () => {
  error.value = ''
  if (!selectedId.value) {
    list.value = []
    return
  }
  try {
    list.value = await request(`/api/admin/lectures/${selectedId.value}/checkins`)
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

onMounted(async () => {
  await loadLectures()
})
</script>

<template>
  <AppShell title="讲座签到">
    <div class="card filter-card">
      <div class="filter-grid">
        <label class="field">
          选择讲座
          <select v-model="selectedId" class="select" @change="loadCheckins">
            <option value="">请选择讲座</option>
            <option v-for="item in lectures" :key="item.id" :value="item.id">
              {{ item.title }} · {{ item.startTime }}
            </option>
          </select>
        </label>
        <div class="filter-actions">
          <button class="btn primary" @click="loadCheckins">刷新签到</button>
        </div>
      </div>
    </div>

    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>用户</th>
            <th>签到时间</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.userId">
            <td>{{ item.username }}</td>
            <td>{{ item.checkInAt }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无签到记录</div>
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
