<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppShell from '@/components/AppShell.vue'
import SeatLayoutEditor from '@/components/SeatLayoutEditor.vue'
import { request } from '@/services/api'

const route = useRoute()
const router = useRouter()
const roomId = ref(route.params.id)
const room = ref(null)
const seats = ref([])
const editMode = ref('grid') // 'grid' 或 'manual'
const error = ref('')
const message = ref('')
const saving = ref(false)

const load = async () => {
  error.value = ''
  try {
    const [roomData, seatsData] = await Promise.all([
      request(`/api/admin/study-rooms/${roomId.value}`),
      request(`/api/admin/study-rooms/${roomId.value}/seats`)
    ])
    room.value = roomData
    seats.value = seatsData || []
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const handleSeatsChange = (newSeats) => {
  seats.value = newSeats
}

// 检测座位编号重名
const checkDuplicateSeatNo = () => {
  const seatNos = seats.value.map(s => s.seatNo)
  const duplicates = seatNos.filter((no, index) => seatNos.indexOf(no) !== index)
  
  if (duplicates.length > 0) {
    const uniqueDuplicates = [...new Set(duplicates)]
    return uniqueDuplicates
  }
  return []
}

const saveSeatLayout = async () => {
  if (seats.value.length === 0) {
    error.value = '请先生成或添加座位'
    return
  }
  
  // 检测重名
  const duplicates = checkDuplicateSeatNo()
  if (duplicates.length > 0) {
    error.value = `座位编号重复：${duplicates.join(', ')}，请修改后再保存`
    return
  }
  
  saving.value = true
  error.value = ''
  message.value = ''
  
  try {
    // 直接批量保存（后端会先删除旧座位）
    await request(`/api/admin/study-rooms/${roomId.value}/seats/batch`, {
      method: 'POST',
      body: JSON.stringify(seats.value)
    })
    
    message.value = `成功保存 ${seats.value.length} 个座位！`
    
    // 更新自习室座位总数
    await request(`/api/admin/study-rooms/${roomId.value}`, {
      method: 'PUT',
      body: JSON.stringify({
        ...room.value,
        totalSeats: seats.value.length
      })
    })
    
    setTimeout(() => {
      router.push('/admin/study-rooms')
    }, 1500)
  } catch (e) {
    error.value = e.message || '保存失败'
    console.error('保存座位布局失败:', e)
  } finally {
    saving.value = false
  }
}

const backToList = () => {
  router.push('/admin/study-rooms')
}

onMounted(load)
</script>

<template>
  <AppShell :title="room ? `${room.name} - 座位管理` : '座位管理'">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div v-if="message" class="card success-card">{{ message }}</div>
    
    <div class="card info-card">
      <div v-if="room" class="room-header">
        <div>
          <h2>{{ room.name }}</h2>
          <p>{{ room.building }} {{ room.floor }}F {{ room.area ? `· ${room.area}` : '' }}</p>
        </div>
        <button class="btn ghost" @click="backToList">返回列表</button>
      </div>
      
      <div class="mode-selector">
        <label class="radio-label">
          <input type="radio" v-model="editMode" value="grid" />
          <span>网格自动生成</span>
        </label>
        <label class="radio-label">
          <input type="radio" v-model="editMode" value="manual" />
          <span>手动拖拽布局</span>
        </label>
      </div>
    </div>
    
    <div class="card editor-card">
      <SeatLayoutEditor
        v-model="seats"
        :mode="editMode"
        :canvas-width="900"
        :canvas-height="600"
        :default-prefix="room?.name || 'A'"
        @change="handleSeatsChange"
      />
    </div>
    
    <div class="action-bar">
      <button class="btn primary large" @click="saveSeatLayout" :disabled="saving || seats.length === 0">
        {{ saving ? '保存中...' : '保存座位布局' }}
      </button>
      <button class="btn ghost" @click="backToList">取消</button>
    </div>
  </AppShell>
</template>

<style scoped>
.error-card {
  padding: 16px;
  color: #dc2626;
  background: #fee2e2;
  margin-bottom: 20px;
}

@media (prefers-color-scheme: dark) {
  .error-card {
    background: #7f1d1d;
    color: #fca5a5;
  }
}

.success-card {
  padding: 16px;
  color: #16a34a;
  background: #d1fae5;
  margin-bottom: 20px;
}

@media (prefers-color-scheme: dark) {
  .success-card {
    background: #064e3b;
    color: #a7f3d0;
  }
}

.info-card {
  padding: 20px;
  margin-bottom: 20px;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

@media (prefers-color-scheme: dark) {
  .room-header {
    border-bottom-color: #333;
  }
}

.room-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .room-header h2 {
    color: #f3f4f6;
  }
}

.room-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

@media (prefers-color-scheme: dark) {
  .room-header p {
    color: #9ca3af;
  }
}

.mode-selector {
  display: flex;
  gap: 24px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  color: #374151;
}

@media (prefers-color-scheme: dark) {
  .radio-label {
    color: #d1d5db;
  }
}

.radio-label input[type="radio"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.editor-card {
  padding: 20px;
  margin-bottom: 20px;
}

.action-bar {
  display: flex;
  gap: 12px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

@media (prefers-color-scheme: dark) {
  .action-bar {
    background: #1f1f1f;
  }
}

.btn.large {
  padding: 12px 32px;
  font-size: 16px;
}
</style>
