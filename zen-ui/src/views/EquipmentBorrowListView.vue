<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/equipments/borrows/my')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const cancel = async (id) => {
  if (!confirm('确认取消该借用申请？')) return
  try {
    await request(`/api/equipments/borrows/${id}/cancel`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '取消失败')
  }
}

const returnEquip = async (id) => {
  if (!confirm('确认归还设备？')) return
  try {
    await request(`/api/equipments/borrows/${id}/return`, { method: 'POST' })
    await load()
  } catch (e) {
    alert(e.message || '归还失败')
  }
}

const statusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '审批中'
    case 'APPROVED':
      return '借用中'
    case 'REJECTED':
      return '已驳回'
    case 'RETURNED':
      return '已归还'
    case 'CANCELED':
      return '已取消'
    default:
      return status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="我的设备">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>设备</th>
            <th>时间</th>
            <th>状态</th>
            <th>用途</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.equipmentName }} {{ item.assetNo ? `(${item.assetNo})` : '' }}</td>
            <td>{{ item.startTime }} - {{ item.endTime }}</td>
            <td><span class="tag">{{ statusText(item.status) }}</span></td>
            <td>{{ item.reason || '-' }}</td>
            <td class="actions">
              <button v-if="item.status === 'PENDING'" class="btn ghost" @click="cancel(item.id)">
                取消
              </button>
              <button v-if="item.status === 'APPROVED'" class="btn primary" @click="returnEquip(item.id)">
                归还
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无借用记录</div>
    </div>
  </AppShell>
</template>

<style scoped>
.table-card {
  padding: 10px 16px 18px;
}

.actions {
  display: flex;
  gap: 8px;
}

.error-card {
  padding: 16px;
  color: #dc2626;
}
</style>
