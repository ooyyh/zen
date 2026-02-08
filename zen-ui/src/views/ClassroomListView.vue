<script setup>
import { ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const query = ref({
  building: '',
  minCapacity: ''
})
const list = ref([])
const loading = ref(false)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const params = new URLSearchParams()
    if (query.value.building) params.append('building', query.value.building)
    if (query.value.minCapacity) params.append('minCapacity', query.value.minCapacity)
    const data = await request(`/api/classrooms?${params.toString()}`)
    list.value = data
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

load()
</script>

<template>
  <AppShell title="教室资源">
    <div class="card filter-card">
      <div class="filter-grid">
        <label class="field">
          楼栋
          <input v-model="query.building" class="input" placeholder="如 A 区" />
        </label>
        <label class="field">
          最小容量
          <input v-model="query.minCapacity" class="input" type="number" placeholder="如 60" />
        </label>
        <div class="filter-actions">
          <button class="btn primary" @click="load">筛选</button>
          <button class="btn ghost" @click="query = { building: '', minCapacity: '' }; load()">重置</button>
        </div>
      </div>
    </div>

    <div v-if="error" class="card error-card">{{ error }}</div>
    <div v-else class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>楼栋</th>
            <th>教室号</th>
            <th>容量</th>
            <th>位置</th>
            <th>设备</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.building }}</td>
            <td>{{ item.roomNo }}</td>
            <td>{{ item.capacity }}</td>
            <td>{{ item.location || '-' }}</td>
            <td>{{ item.equipmentJson || '-' }}</td>
            <td>
              <span class="tag" v-if="item.status === 1">可用</span>
              <span class="tag" v-else>停用</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length && !loading" class="empty">暂无教室数据，请在后台管理中添加。</div>
    </div>
  </AppShell>
</template>

<style scoped>
.filter-card {
  padding: 16px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
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

@media (max-width: 900px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
}
</style>
