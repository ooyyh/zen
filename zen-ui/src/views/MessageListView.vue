<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const loading = ref(false)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    list.value = await request('/api/messages')
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const markRead = async (id) => {
  try {
    await request(`/api/messages/${id}/read`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '操作失败')
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="通知中心">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card list-card">
      <div v-if="!list.length && !loading" class="empty">暂无通知消息</div>
      <div v-for="msg in list" :key="msg.id" class="msg-item">
        <div>
          <h4>{{ msg.title }}</h4>
          <p class="subtle">{{ msg.content }}</p>
          <span class="helper">{{ msg.createdAt }}</span>
        </div>
        <div class="msg-actions">
          <span class="tag" v-if="msg.status === 'UNREAD'">未读</span>
          <span class="tag" v-else>已读</span>
          <button v-if="msg.status === 'UNREAD'" class="btn ghost" @click="markRead(msg.id)">标为已读</button>
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

.msg-item {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 12px;
  background: #ffffff;
}

.msg-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}

@media (max-width: 720px) {
  .msg-item {
    flex-direction: column;
  }
}
</style>
