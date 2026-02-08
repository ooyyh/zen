<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const trips = ref([])
const bookingMap = ref({})
const error = ref('')

const load = async () => {
  error.value = ''
  try {
    const [tripList, myBookings] = await Promise.all([
      request('/api/bus/trips?status=OPEN'),
      request('/api/bus/bookings/my')
    ])
    trips.value = tripList
    const map = {}
    myBookings.forEach((item) => {
      map[item.tripId] = item.status
    })
    bookingMap.value = map
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const book = async (tripId) => {
  try {
    await request(`/api/bus/trips/${tripId}/book`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '预约失败')
  }
}

const statusLabel = (status) => {
  switch (status) {
    case 'BOOKED':
      return '已预约'
    case 'WAITLIST':
      return '候补中'
    case 'CANCELED':
      return '已取消'
    default:
      return ''
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="校车预约">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card list-card">
      <div v-if="!trips.length" class="empty">暂无可预约班次</div>
      <div v-for="trip in trips" :key="trip.id" class="trip-item">
        <div>
          <h4>{{ trip.origin }} → {{ trip.destination }}</h4>
          <p class="subtle">线路：{{ trip.routeName }} · 车次 {{ trip.busNo || '待定' }}</p>
          <span class="helper">{{ trip.departureTime }} - {{ trip.arrivalTime }}</span>
        </div>
        <div class="trip-actions">
          <span class="tag">余位 {{ trip.remainingSeats }}</span>
          <span v-if="bookingMap[trip.id]" class="tag">{{ statusLabel(bookingMap[trip.id]) }}</span>
          <button class="btn primary" :disabled="!!bookingMap[trip.id]" @click="book(trip.id)">
            {{ bookingMap[trip.id] ? '已预约' : '预约' }}
          </button>
        </div>
      </div>
    </div>
  </AppShell>
</template>

<style scoped>
.list-card {
  padding: 16px;
  display: grid;
  gap: 14px;
}

.trip-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 12px;
  background: #ffffff;
}

.trip-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}

@media (max-width: 720px) {
  .trip-item {
    flex-direction: column;
  }

  .trip-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>
