# WebSocket实时座位状态同步功能

## 安装依赖

```bash
cd zen-ui
npm install sockjs-client @stomp/stompjs
```

## 后端已完成
✅ 添加 WebSocket 配置
✅ 添加座位状态广播服务
✅ 添加 pending/release API

## 前端需要手动修改

### 文件：`zen-ui/src/views/SeatSelectionView.vue`

找到以下代码段并替换：

#### 1. 替换 `const reserve` 函数（第186行）
```javascript
// 旧代码
const reserve = (seat) => {
  if (seat.reserved) return
  const seatLabel = seat.seatNo || seat.id || '此座位'
  showDialog('预约确认', `确认预约座位 ${seatLabel}？`, 'confirm', seat)
}

// 新代码
const reserve = async (seat) => {
  if (seat.reserved || pendingSeats.value.has(seat.id)) return
  
  try {
    await request(`/api/seat-status/pending/${seat.id}`, { method: 'POST' })
    pendingSeats.value.set(seat.id, currentUserId.value)
    drawSeats()
    
    const seatLabel = seat.seatNo || seat.id || 'this seat'
    showDialog('Confirm Reservation', `Reserve seat ${seatLabel}?`, 'confirm', seat)
  } catch (e) {
    console.error('Failed to mark pending:', e)
  }
}
```

#### 2. 替换 `closeDialog` 函数（第172行）
```javascript
// 旧代码
const closeDialog = () => {
  dialog.value.show = false
  if (!dialog.value.seat) {
    selectedSeatId.value = null
    drawSeats()
  }
}

// 新代码
const closeDialog = () => {
  const oldSeat = dialog.value.seat
  dialog.value.show = false
  
  if (oldSeat && !dialog.value.confirmed) {
    request(`/api/seat-status/release/${oldSeat.id}`, { method: 'POST' }).catch(() => {})
    pendingSeats.value.delete(oldSeat.id)
    selectedSeatId.value = null
    drawSeats()
  }
}
```

#### 3. 在 `load` 函数末尾添加（第42-56行）
```javascript
const load = async () => {
  error.value = ''
  try {
    const [roomData, userData] = await Promise.all([
      request(`/api/study-rooms/${roomId.value}`),
      request('/api/user/profile')
    ])
    room.value = roomData
    currentUserId.value = userData.id
    
    // ...原有代码...
    
    // 添加这段：连接WebSocket
    websocket.connect(() => {
      websocket.subscribe('/topic/seat-status', handleSeatStatusUpdate)
    })
  } catch (e) {
    error.value = e.message || 'Loading failed'
  }
}
```

#### 4. 添加 WebSocket 消息处理函数（在 drawSeats 后面）
```javascript
const handleSeatStatusUpdate = (data) => {
  console.log('Seat status update:', data)
  
  if (data.status === 'PENDING') {
    pendingSeats.value.set(data.seatId, data.userId)
  } else if (data.status === 'AVAILABLE') {
    pendingSeats.value.delete(data.seatId)
  } else if (data.status === 'RESERVED') {
    pendingSeats.value.delete(data.seatId)
    loadSeats()
    return
  }
  
  drawSeats()
}
```

#### 5. 添加清理函数（在 onMounted 前）
```javascript
onUnmounted(() => {
  websocket.disconnect()
})
```

## 测试步骤

1. 重启后端服务
2. 打开两个浏览器窗口/标签页
3. 都进入同一个自习室的座位选择页面
4. 在窗口A点击一个座位
5. 立即在窗口B刷新或观察
6. 窗口B应该能看到该座位变成橙色（预约中）

## 效果
- 🟢 绿色 = 可预约
- 🟠 橙色 = 有人正在预约（实时同步）
- 🔴 红色 = 已被占用
- 🔵 蓝色 = 当前选中
