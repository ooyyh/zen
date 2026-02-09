<script setup>
import { onMounted, onUnmounted, ref, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppShell from '@/components/AppShell.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { request } from '@/services/api'
import websocket from '@/services/websocket'

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
  pending: '#f59e0b',  // 预约中的颜色（橙色）
  power: '#fbbf24',
  text: '#ffffff'
}

const selectedSeatId = ref(null)
const pendingSeats = ref(new Map())  // Map<seatId, {userId, username}>
const currentUserId = ref(null)

const dialog = ref({
  show: false,
  title: '',
  message: '',
  type: 'confirm',
  seat: null
})

const load = async () => {
  error.value = ''
  try {
    const [roomData, userData] = await Promise.all([
      request(`/api/study-rooms/${roomId.value}`),
      request('/api/user/profile')
    ])
    room.value = roomData
    currentUserId.value = userData.id
    
    // 默认时间：现在到4小时后
    const now = new Date()
    const later = new Date(now.getTime() + 4 * 60 * 60 * 1000)
    startTime.value = now.toISOString().slice(0, 16)
    endTime.value = later.toISOString().slice(0, 16)
    
    await loadSeats()
    
    // 加载当前pending的座位状态
    await loadPendingSeats()
    
    // 连接WebSocket并订阅座位状态
    websocket.connect(() => {
      websocket.subscribe('/topic/seat-status', handleSeatStatusUpdate)
    })
  } catch (e) {
    error.value = e.message || 'Loading failed'
  }
}

const loadPendingSeats = async () => {
  try {
    const data = await request(`/api/seat-status/pending/${roomId.value}`)
    console.log('Loaded pending seats:', data)
    // data 是 Map<seatId, {userId, username}>
    pendingSeats.value.clear()
    for (const [seatIdStr, info] of Object.entries(data)) {
      const seatId = parseInt(seatIdStr)
      pendingSeats.value.set(seatId, info)
      console.log(`Set pending seat ${seatId}:`, info)
    }
    console.log('Final pendingSeats Map:', pendingSeats.value)
    await nextTick()
    drawSeats()
  } catch (e) {
    console.error('Failed to load pending seats:', e)
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
    
    let color = COLORS.available
    const isPending = pendingSeats.value.has(seat.id)
    
    if (seat.reserved) {
      color = COLORS.reserved
    } else if (isPending) {
      color = COLORS.pending
    } else if (seat.id === selectedSeatId.value) {
      color = COLORS.selected
    }
    
    // 座位矩形
    ctx.value.fillStyle = color
    ctx.value.fillRect(x, y, SEAT_SIZE, SEAT_SIZE)
    
    // 座位边框
    ctx.value.strokeStyle = '#333'
    ctx.value.lineWidth = 1
    ctx.value.strokeRect(x, y, SEAT_SIZE, SEAT_SIZE)
    
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
    
    // 如果是预约中状态，显示用户名
    if (isPending) {
      const pendingInfo = pendingSeats.value.get(seat.id)
      if (pendingInfo && pendingInfo.username) {
        ctx.value.fillStyle = 'rgba(0, 0, 0, 0.7)'
        ctx.value.fillRect(x, y + SEAT_SIZE - 15, SEAT_SIZE, 15)
        ctx.value.fillStyle = '#fff'
        ctx.value.font = '10px sans-serif'
        ctx.value.fillText(pendingInfo.username, x + SEAT_SIZE / 2, y + SEAT_SIZE - 7)
      }
    }
  })
}

const handleSeatStatusUpdate = (data) => {
  console.log('Seat status update:', data)
  
  if (data.status === 'PENDING') {
    // 有人开始预约，保存用户信息
    pendingSeats.value.set(data.seatId, {
      userId: data.userId,
      username: data.username || `User${data.userId}`
    })
    drawSeats()
  } else if (data.status === 'AVAILABLE') {
    // 有人取消预约
    pendingSeats.value.delete(data.seatId)
    drawSeats()
  } else if (data.status === 'RESERVED') {
    // 预约成功
    pendingSeats.value.delete(data.seatId)
    
    const seat = seats.value.find(s => s.id === data.seatId)
    if (seat) {
      seat.reserved = true
      drawSeats()
    }
    
    setTimeout(() => loadSeats(), 500)
  }
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

const availableSeats = computed(() => seats.value.filter(s => !s.reserved && !pendingSeats.value.has(s.id)))
const reservedSeats = computed(() => seats.value.filter(s => s.reserved))
const pendingCount = computed(() => pendingSeats.value.size)

const showDialog = (title, message, type, seat = null) => {
  dialog.value = { show: true, title, message, type, seat, confirmed: false }
}

const closeDialog = () => {
  const oldSeat = dialog.value.seat
  const wasConfirmed = dialog.value.confirmed
  dialog.value.show = false
  
  // 如果取消预约，移除pending状态并通知后端
  if (oldSeat && !wasConfirmed) {
    request(`/api/seat-status/release/${oldSeat.id}`, { method: 'POST' }).catch(() => {})
    pendingSeats.value.delete(oldSeat.id)
    selectedSeatId.value = null
    drawSeats()
  }
}

const handleDialogConfirm = async () => {
  dialog.value.confirmed = true
  if (dialog.value.seat) {
    await doReserve(dialog.value.seat)
  }
}

const reserve = async (seat) => {
  if (seat.reserved || pendingSeats.value.has(seat.id)) return
  
  // 立即标记为预约中并通知后端
  try {
    await request(`/api/seat-status/pending/${seat.id}`, { method: 'POST' })
    
    // 本地也立即显示（不等WebSocket）
    const userData = await request('/api/user/profile').catch(() => ({ username: 'You' }))
    pendingSeats.value.set(seat.id, {
      userId: currentUserId.value,
      username: userData.username || 'You'
    })
    drawSeats()
    
    const seatLabel = seat.seatNo || seat.id || 'this seat'
    showDialog('Confirm Reservation', `Reserve seat ${seatLabel}?`, 'confirm', seat)
  } catch (e) {
    console.error('Failed to mark pending:', e)
  }
}

const doReserve = async (seat) => {
  try {
    await request(`/api/study-rooms/seats/${seat.id}/reserve`, {
      method: 'POST',
      body: JSON.stringify({
        startTime: new Date(startTime.value).toISOString(),
        endTime: new Date(endTime.value).toISOString()
      })
    })
    // 预约成功，pending状态会通过WebSocket广播移除
    showDialog('Success', 'Reservation successful!', 'success', null)
    setTimeout(() => {
      router.push('/seat-reservations/my')
    }, 1500)
  } catch (e) {
    // 预约失败，释放pending状态
    await request(`/api/seat-status/release/${seat.id}`, { method: 'POST' }).catch(() => {})
    pendingSeats.value.delete(seat.id)
    showDialog('Error', e.message || 'Reservation failed', 'error', null)
    selectedSeatId.value = null
    await loadSeats()
  }
}

onUnmounted(() => {
  websocket.disconnect()
})

onMounted(load)
</script>

<template>
  <AppShell :title="room ? room.name : '选择座位'">
    <ConfirmDialog
      :show="dialog.show"
      :title="dialog.title"
      :message="dialog.message"
      :type="dialog.type"
      @confirm="handleDialogConfirm"
      @close="closeDialog"
    />
    
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
        <span class="stat available">Available: {{ availableSeats.length }}</span>
        <span class="stat reserved">Reserved: {{ reservedSeats.length }}</span>
        <span v-if="pendingCount > 0" class="stat pending">Reserving: {{ pendingCount }}</span>
      </div>
    </div>

    <div class="card seats-card">
      <canvas ref="canvas" @click="handleCanvasClick" class="seat-canvas"></canvas>
    </div>
    
    <div class="legend-card card">
      <div class="legend-item">
        <div class="legend-box" style="background: #10b981"></div>
        <span>Available</span>
      </div>
      <div class="legend-item">
        <div class="legend-box" style="background: #f59e0b"></div>
        <span>Reserving</span>
      </div>
      <div class="legend-item">
        <div class="legend-box" style="background: #ef4444"></div>
        <span>Reserved</span>
      </div>
      <div class="legend-item">
        <div class="legend-box" style="background: #3b82f6"></div>
        <span>Selected</span>
      </div>
      <div class="legend-item">
        <div class="legend-circle" style="background: #fbbf24"></div>
        <span>Has Power</span>
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

.stat.pending {
  background: #fef3c7;
  color: #92400e;
  font-weight: 600;
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
  .stat.pending {
    background: #78350f;
    color: #fde68a;
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
