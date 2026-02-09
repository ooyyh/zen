<script setup>
import { onMounted, ref, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const route = useRoute()
const router = useRouter()
const roomId = ref(route.params.id)
const room = ref(null)
const seats = ref([])
const error = ref('')
const startTime = ref('')
const endTime = ref('')
const canvas = ref(null)
const ctx = ref(null)

const SEAT_SIZE = 40
const COLORS = {
  available: '#10b981',
  reserved: '#ef4444',
  selected: '#3b82f6',
  power: '#fbbf24',
  text: '#ffffff'
}

const selectedSeatId = ref(null)

const load = async () => {
  error.value = ''
  try {
    room.value = await request(`/api/study-rooms/${roomId.value}`)
    
    // 默认时间：现在到4小时后
    const now = new Date()
    const later = new Date(now.getTime() + 4 * 60 * 60 * 1000)
    startTime.value = now.toISOString().slice(0, 16)
    endTime.value = later.toISOString().slice(0, 16)
    
    await loadSeats()
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const loadSeats = async () => {
  if (!startTime.value || !endTime.value) return
  try {
    const params = new URLSearchParams({
      startTime: new Date(startTime.value).toISOString(),
      endTime: new Date(endTime.value).toISOString()
    })
    seats.value = await request(`/api/study-rooms/${roomId.value}/seats?${params}`)
    await nextTick()
    drawSeats()
  } catch (e) {
    error.value = e.message || '加载座位失败'
  }
}

const drawSeats = () => {
  if (!canvas.value) return
  ctx.value = canvas.value.getContext('2d')
  
  // 计算画布大小
  let maxX = 0, maxY = 0
  seats.value.forEach(seat => {
    if (seat.positionX) maxX = Math.max(maxX, seat.positionX + SEAT_SIZE)
    if (seat.positionY) maxY = Math.max(maxY, seat.positionY + SEAT_SIZE)
  })
  
  canvas.value.width = Math.max(900, maxX + 50)
  canvas.value.height = Math.max(600, maxY + 50)
  
  // 清空画布
  ctx.value.clearRect(0, 0, canvas.value.width, canvas.value.height)
  
  // 绘制网格背景
  ctx.value.strokeStyle = '#f3f4f6'
  ctx.value.lineWidth = 1
  for (let i = 0; i <= canvas.value.width; i += 50) {
    ctx.value.beginPath()
    ctx.value.moveTo(i, 0)
    ctx.value.lineTo(i, canvas.value.height)
    ctx.value.stroke()
  }
  for (let i = 0; i <= canvas.value.height; i += 50) {
    ctx.value.beginPath()
    ctx.value.moveTo(0, i)
    ctx.value.lineTo(canvas.value.width, i)
    ctx.value.stroke()
  }
  
  // 绘制座位
  seats.value.forEach(seat => {
    const x = seat.positionX || 0
    const y = seat.positionY || 0
    const isSelected = selectedSeatId.value === seat.id
    const color = isSelected ? COLORS.selected : (seat.reserved ? COLORS.reserved : COLORS.available)
    
    // 座位矩形
    ctx.value.fillStyle = color
    ctx.value.fillRect(x, y, SEAT_SIZE, SEAT_SIZE)
    
    // 座位编号
    ctx.value.fillStyle = COLORS.text
    ctx.value.font = '12px sans-serif'
    ctx.value.textAlign = 'center'
    ctx.value.textBaseline = 'middle'
    ctx.value.fillText(seat.seatNo, x + SEAT_SIZE / 2, y + SEAT_SIZE / 2)
    
    // 电源图标
    if (seat.hasPower) {
      ctx.value.fillStyle = COLORS.power
      ctx.value.beginPath()
      ctx.value.arc(x + SEAT_SIZE - 8, y + 8, 6, 0, Math.PI * 2)
      ctx.value.fill()
    }
  })
}

const handleCanvasClick = (e) => {
  const rect = canvas.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  // 检测点击的座位
  for (const seat of seats.value) {
    const sx = seat.positionX || 0
    const sy = seat.positionY || 0
    if (x >= sx && x <= sx + SEAT_SIZE && y >= sy && y <= sy + SEAT_SIZE) {
      if (!seat.reserved) {
        selectedSeatId.value = seat.id
        drawSeats()
        reserve(seat)
      }
      return
    }
  }
}

const availableSeats = computed(() => seats.value.filter(s => !s.reserved))
const reservedSeats = computed(() => seats.value.filter(s => s.reserved))

const reserve = async (seat) => {
  if (seat.reserved) return
  if (!confirm(`确认预约座位 ${seat.seatNo}？`)) {
    selectedSeatId.value = null
    drawSeats()
    return
  }
  
  try {
    await request(`/api/study-rooms/seats/${seat.id}/reserve`, {
      method: 'POST',
      body: JSON.stringify({
        startTime: new Date(startTime.value).toISOString(),
        endTime: new Date(endTime.value).toISOString()
      })
    })
    alert('预约成功！')
    router.push('/seat-reservations/my')
  } catch (e) {
    alert(e.message || '预约失败')
    selectedSeatId.value = null
    await loadSeats()
  }
}

onMounted(load)
</script>

<template>
  <AppShell :title="room ? room.name : '选择座位'">
    <div v-if="error" class="card error-card">{{ error }}</div>
    
    <div class="card filter-card">
      <div class="room-info" v-if="room">
        <h3>{{ room.name }}</h3>
        <span class="building">{{ room.building }} {{ room.floor }}F</span>
        <span v-if="room.area" class="area">{{ room.area }}</span>
      </div>
      
      <div class="time-selector">
        <div class="input-group">
          <label>开始时间</label>
          <input type="datetime-local" v-model="startTime" @change="loadSeats" />
        </div>
        <div class="input-group">
          <label>结束时间</label>
          <input type="datetime-local" v-model="endTime" @change="loadSeats" />
        </div>
        <button class="btn primary" @click="loadSeats">查询</button>
      </div>
      
      <div class="stats">
        <span class="stat available">可用: {{ availableSeats.length }}</span>
        <span class="stat reserved">已占: {{ reservedSeats.length }}</span>
      </div>
    </div>

    <div class="card seats-card">
      <canvas ref="canvas" @click="handleCanvasClick" class="seat-canvas"></canvas>
    </div>
    
    <div class="legend-card card">
      <div class="legend-item">
        <div class="legend-box" style="background: #10b981"></div>
        <span>可预约</span>
      </div>
      <div class="legend-item">
        <div class="legend-box" style="background: #ef4444"></div>
        <span>已占用</span>
      </div>
      <div class="legend-item">
        <div class="legend-circle" style="background: #fbbf24"></div>
        <span>有电源</span>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.error-card {
  padding: 16px;
  color: #dc2626;
  margin-bottom: 20px;
}

.filter-card {
  padding: 20px;
  margin-bottom: 20px;
}

.room-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.room-info h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.building, .area {
  padding: 4px 10px;
  background: #e0e7ff;
  color: #4338ca;
  border-radius: 12px;
  font-size: 13px;
}

@media (prefers-color-scheme: dark) {
  .building, .area {
    background: #312e81;
    color: #c7d2fe;
  }
}

.time-selector {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 16px;
}

.input-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.input-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

@media (prefers-color-scheme: dark) {
  .input-group label {
    color: #d1d5db;
  }
}

.input-group input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .input-group input {
    background: #2a2a2a;
    border-color: #444;
    color: #f3f4f6;
  }
}

.stats {
  display: flex;
  gap: 20px;
}

.stat {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
}

.stat.available {
  background: #d1fae5;
  color: #065f46;
}

.stat.reserved {
  background: #fee2e2;
  color: #991b1b;
}

@media (prefers-color-scheme: dark) {
  .stat.available {
    background: #064e3b;
    color: #a7f3d0;
  }
  .stat.reserved {
    background: #7f1d1d;
    color: #fca5a5;
  }
}

.seats-card {
  padding: 20px;
  overflow-x: auto;
}

.seat-canvas {
  display: block;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  background: white;
}

@media (prefers-color-scheme: dark) {
  .seat-canvas {
    border-color: #444;
    background: #1a1a1a;
  }
}

.legend-card {
  padding: 16px 20px;
  display: flex;
  gap: 24px;
  align-items: center;
  background: #f9fafb;
}

@media (prefers-color-scheme: dark) {
  .legend-card {
    background: #1f1f1f;
  }
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}

@media (prefers-color-scheme: dark) {
  .legend-item {
    color: #9ca3af;
  }
}

.legend-box {
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.legend-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
</style>
