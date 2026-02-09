<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const router = useRouter()
const list = ref([])
const error = ref('')

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/study-rooms')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const selectRoom = (room) => {
  router.push(`/study-rooms/${room.id}/seats`)
}

onMounted(load)
</script>

<template>
  <AppShell title="自习室抢座">
    <div v-if="error" class="card error-card">{{ error }}</div>
    
    <div class="room-grid">
      <div v-for="room in list" :key="room.id" class="room-card card" @click="selectRoom(room)">
        <div class="room-header">
          <h3>{{ room.name }}</h3>
          <span class="building-tag">{{ room.building }}</span>
        </div>
        <div class="room-info">
          <div class="info-item">
            <span class="label">楼层：</span>
            <span>{{ room.floor }}F</span>
          </div>
          <div v-if="room.area" class="info-item">
            <span class="label">区域：</span>
            <span>{{ room.area }}</span>
          </div>
          <div class="info-item">
            <span class="label">座位数：</span>
            <span>{{ room.totalSeats }}</span>
          </div>
        </div>
        <button class="btn primary full-width">选择座位</button>
      </div>
    </div>

    <div v-if="!list.length && !error" class="empty">暂无可用自习室</div>
  </AppShell>
</template>

<style scoped>
.error-card {
  padding: 16px;
  color: #dc2626;
  margin-bottom: 20px;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.room-card {
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.room-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.room-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .room-header h3 {
    color: #f3f4f6;
  }
}

.building-tag {
  padding: 4px 10px;
  background: #e0e7ff;
  color: #4338ca;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

@media (prefers-color-scheme: dark) {
  .building-tag {
    background: #312e81;
    color: #c7d2fe;
  }
}

.room-info {
  margin-bottom: 20px;
}

.info-item {
  padding: 8px 0;
  font-size: 14px;
  color: #4b5563;
  display: flex;
}

@media (prefers-color-scheme: dark) {
  .info-item {
    color: #9ca3af;
  }
}

.info-item .label {
  font-weight: 500;
  margin-right: 8px;
  color: #6b7280;
}

@media (prefers-color-scheme: dark) {
  .info-item .label {
    color: #9ca3af;
  }
}

.full-width {
  width: 100%;
}
</style>
