<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const signupMap = ref({})
const loading = ref(false)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const [lectures, signups] = await Promise.all([
      request('/api/lectures?status=OPEN'),
      request('/api/lectures/my/signups')
    ])
    list.value = lectures
    const map = {}
    signups.forEach((item) => {
      map[item.lectureId] = item.status
    })
    signupMap.value = map
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const signup = async (lectureId) => {
  try {
    await request(`/api/lectures/${lectureId}/signup`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '报名失败')
  }
}

const statusLabel = (status) => {
  switch (status) {
    case 'SIGNED_UP':
      return '已报名'
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
  <AppShell title="讲座报名">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card list-card">
      <div v-if="!list.length && !loading" class="empty">暂无可报名讲座</div>
      <div v-for="item in list" :key="item.id" class="lecture-item">
        <div>
          <h4>{{ item.title }}</h4>
          <p class="subtle">{{ item.speaker || '待定讲者' }} · {{ item.location || '待定地点' }}</p>
          <span class="helper">{{ item.startTime }} - {{ item.endTime }}</span>
        </div>
        <div class="lecture-actions">
          <span class="tag">容量 {{ item.capacity }}</span>
          <span v-if="signupMap[item.id]" class="tag">{{ statusLabel(signupMap[item.id]) }}</span>
          <button class="btn primary" :disabled="!!signupMap[item.id]" @click="signup(item.id)">
            {{ signupMap[item.id] ? '已报名' : '报名' }}
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

.lecture-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 12px;
  background: #ffffff;
}

.lecture-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}

@media (max-width: 720px) {
  .lecture-item {
    flex-direction: column;
  }

  .lecture-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>
