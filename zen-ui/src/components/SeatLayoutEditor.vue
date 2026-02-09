<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'

const props = defineProps({
  modelValue: Array, // 座位数组
  mode: {
    type: String,
    default: 'grid' // 'grid' 或 'manual'
  },
  canvasWidth: {
    type: Number,
    default: 800
  },
  canvasHeight: {
    type: Number,
    default: 600
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const canvas = ref(null)
const ctx = ref(null)
const seats = ref([])
const selectedSeat = ref(null)
const draggingSeat = ref(null)
const dragOffset = ref({ x: 0, y: 0 })

// 网格模式参数
const gridRows = ref(5)
const gridCols = ref(10)
const seatPrefix = ref('A')

// 座位样式
const SEAT_SIZE = 40
const SEAT_SPACING = 10
const COLORS = {
  seat: '#3b82f6',
  seatHover: '#2563eb',
  seatSelected: '#10b981',
  power: '#fbbf24',
  text: '#ffffff'
}

onMounted(() => {
  if (canvas.value) {
    ctx.value = canvas.value.getContext('2d')
    if (props.modelValue && props.modelValue.length > 0) {
      seats.value = [...props.modelValue]
    }
    draw()
  }
})

watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    seats.value = [...newVal]
    draw()
  }
}, { deep: true })

// 网格模式：自动生成座位
const generateGrid = () => {
  const newSeats = []
  const startX = 50
  const startY = 50
  
  for (let row = 0; row < gridRows.value; row++) {
    for (let col = 0; col < gridCols.value; col++) {
      const seatNo = `${seatPrefix.value}${String(row + 1).padStart(2, '0')}-${String(col + 1).padStart(2, '0')}`
      newSeats.push({
        seatNo,
        positionX: startX + col * (SEAT_SIZE + SEAT_SPACING),
        positionY: startY + row * (SEAT_SIZE + SEAT_SPACING),
        hasPower: 0,
        seatType: '单人'
      })
    }
  }
  
  seats.value = newSeats
  emit('update:modelValue', seats.value)
  emit('change', seats.value)
  nextTick(() => draw())
}

// 手动模式：添加新座位
const addSeat = () => {
  const newSeat = {
    seatNo: `S${seats.value.length + 1}`,
    positionX: 100 + (seats.value.length % 5) * 60,
    positionY: 100 + Math.floor(seats.value.length / 5) * 60,
    hasPower: 0,
    seatType: '单人'
  }
  seats.value.push(newSeat)
  emit('update:modelValue', seats.value)
  emit('change', seats.value)
  draw()
}

// 删除选中的座位
const deleteSeat = () => {
  if (selectedSeat.value !== null) {
    seats.value.splice(selectedSeat.value, 1)
    selectedSeat.value = null
    emit('update:modelValue', seats.value)
    emit('change', seats.value)
    draw()
  }
}

// 切换电源
const togglePower = () => {
  if (selectedSeat.value !== null) {
    seats.value[selectedSeat.value].hasPower = seats.value[selectedSeat.value].hasPower ? 0 : 1
    emit('update:modelValue', seats.value)
    emit('change', seats.value)
    draw()
  }
}

// 清空所有座位
const clearAll = () => {
  if (confirm('确认清空所有座位？')) {
    seats.value = []
    selectedSeat.value = null
    emit('update:modelValue', seats.value)
    emit('change', seats.value)
    draw()
  }
}

// 绘制座位
const draw = () => {
  if (!ctx.value) return
  
  // 清空画布
  ctx.value.clearRect(0, 0, props.canvasWidth, props.canvasHeight)
  
  // 绘制网格背景
  ctx.value.strokeStyle = '#e5e7eb'
  ctx.value.lineWidth = 1
  for (let i = 0; i <= props.canvasWidth; i += 50) {
    ctx.value.beginPath()
    ctx.value.moveTo(i, 0)
    ctx.value.lineTo(i, props.canvasHeight)
    ctx.value.stroke()
  }
  for (let i = 0; i <= props.canvasHeight; i += 50) {
    ctx.value.beginPath()
    ctx.value.moveTo(0, i)
    ctx.value.lineTo(props.canvasWidth, i)
    ctx.value.stroke()
  }
  
  // 绘制座位
  seats.value.forEach((seat, index) => {
    const isSelected = selectedSeat.value === index
    const color = isSelected ? COLORS.seatSelected : COLORS.seat
    
    // 座位矩形
    ctx.value.fillStyle = color
    ctx.value.fillRect(seat.positionX, seat.positionY, SEAT_SIZE, SEAT_SIZE)
    
    // 座位编号
    ctx.value.fillStyle = COLORS.text
    ctx.value.font = '12px sans-serif'
    ctx.value.textAlign = 'center'
    ctx.value.textBaseline = 'middle'
    ctx.value.fillText(seat.seatNo, seat.positionX + SEAT_SIZE / 2, seat.positionY + SEAT_SIZE / 2)
    
    // 电源图标
    if (seat.hasPower) {
      ctx.value.fillStyle = COLORS.power
      ctx.value.beginPath()
      ctx.value.arc(seat.positionX + SEAT_SIZE - 8, seat.positionY + 8, 6, 0, Math.PI * 2)
      ctx.value.fill()
    }
  })
}

// 鼠标事件处理
const handleMouseDown = (e) => {
  const rect = canvas.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  // 检测点击的座位
  for (let i = seats.value.length - 1; i >= 0; i--) {
    const seat = seats.value[i]
    if (x >= seat.positionX && x <= seat.positionX + SEAT_SIZE &&
        y >= seat.positionY && y <= seat.positionY + SEAT_SIZE) {
      selectedSeat.value = i
      draggingSeat.value = i
      dragOffset.value = {
        x: x - seat.positionX,
        y: y - seat.positionY
      }
      draw()
      return
    }
  }
  
  selectedSeat.value = null
  draw()
}

const handleMouseMove = (e) => {
  if (draggingSeat.value !== null && props.mode === 'manual') {
    const rect = canvas.value.getBoundingClientRect()
    const x = e.clientX - rect.left
    const y = e.clientY - rect.top
    
    seats.value[draggingSeat.value].positionX = Math.max(0, Math.min(props.canvasWidth - SEAT_SIZE, x - dragOffset.value.x))
    seats.value[draggingSeat.value].positionY = Math.max(0, Math.min(props.canvasHeight - SEAT_SIZE, y - dragOffset.value.y))
    
    emit('update:modelValue', seats.value)
    draw()
  }
}

const handleMouseUp = () => {
  if (draggingSeat.value !== null) {
    emit('change', seats.value)
  }
  draggingSeat.value = null
}

// 键盘事件
const handleKeyDown = (e) => {
  if (e.key === 'Delete' && selectedSeat.value !== null) {
    deleteSeat()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
})
</script>

<template>
  <div class="seat-editor">
    <div class="toolbar">
      <div v-if="mode === 'grid'" class="grid-controls">
        <h3>网格生成模式</h3>
        <div class="input-row">
          <label>
            行数
            <input v-model.number="gridRows" type="number" min="1" max="20" />
          </label>
          <label>
            列数
            <input v-model.number="gridCols" type="number" min="1" max="30" />
          </label>
          <label>
            编号前缀
            <input v-model="seatPrefix" type="text" maxlength="3" placeholder="A" />
          </label>
          <button class="btn primary" @click="generateGrid">生成座位</button>
        </div>
      </div>
      
      <div v-if="mode === 'manual'" class="manual-controls">
        <h3>手动拖拽模式</h3>
        <div class="button-row">
          <button class="btn primary" @click="addSeat">添加座位</button>
          <button class="btn ghost" @click="togglePower" :disabled="selectedSeat === null">
            {{ selectedSeat !== null && seats[selectedSeat]?.hasPower ? '移除电源' : '添加电源' }}
          </button>
          <button class="btn ghost" @click="deleteSeat" :disabled="selectedSeat === null">删除座位</button>
          <button class="btn ghost" @click="clearAll">清空全部</button>
        </div>
      </div>
      
      <div class="info">
        <span>座位总数: {{ seats.length }}</span>
        <span v-if="selectedSeat !== null">已选中: {{ seats[selectedSeat]?.seatNo }}</span>
      </div>
    </div>
    
    <div class="canvas-container">
      <canvas
        ref="canvas"
        :width="canvasWidth"
        :height="canvasHeight"
        @mousedown="handleMouseDown"
        @mousemove="handleMouseMove"
        @mouseup="handleMouseUp"
        @mouseleave="handleMouseUp"
      ></canvas>
    </div>
    
    <div class="legend">
      <div class="legend-item">
        <div class="color-box" style="background: #3b82f6"></div>
        <span>普通座位</span>
      </div>
      <div class="legend-item">
        <div class="color-box" style="background: #10b981"></div>
        <span>选中座位</span>
      </div>
      <div class="legend-item">
        <div class="color-circle" style="background: #fbbf24"></div>
        <span>有电源</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.seat-editor {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.toolbar {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

@media (prefers-color-scheme: dark) {
  .toolbar {
    background: #1f1f1f;
  }
}

.toolbar h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .toolbar h3 {
    color: #f3f4f6;
  }
}

.input-row,
.button-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.input-row label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

@media (prefers-color-scheme: dark) {
  .input-row label {
    color: #d1d5db;
  }
}

.input-row input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  width: 100px;
  background: white;
  color: #111827;
}

@media (prefers-color-scheme: dark) {
  .input-row input {
    background: #2a2a2a;
    border-color: #444;
    color: #f3f4f6;
  }
}

.info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #6b7280;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

@media (prefers-color-scheme: dark) {
  .info {
    color: #9ca3af;
    border-top-color: #333;
  }
}

.canvas-container {
  border: 2px solid #d1d5db;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

@media (prefers-color-scheme: dark) {
  .canvas-container {
    border-color: #444;
    background: #1a1a1a;
  }
}

canvas {
  display: block;
  cursor: pointer;
}

.legend {
  display: flex;
  gap: 24px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

@media (prefers-color-scheme: dark) {
  .legend {
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

.color-box {
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.color-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
</style>
