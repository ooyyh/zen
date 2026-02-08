<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/admin/equipments/borrows')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const handle = async (item, action) => {
  const remark = action === 'reject' ? prompt('请输入驳回原因') : ''
  if (action === 'reject' && remark === null) return
  try {
    await request(`/api/admin/equipments/borrows/${item.id}/${action}`, {
      method: 'POST',
      body: JSON.stringify({ remark })
    })
    await load()
  } catch (e) {
    alert(e.message || '操作失败')
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="借用审批">
    <div v-if="error" class="card error-card">{{ error }}</div>
    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>申请人</th>
            <th>设备</th>
            <th>时间</th>
            <th>用途</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.username }}</td>
            <td>{{ item.equipmentName }} {{ item.assetNo ? `(${item.assetNo})` : '' }}</td>
            <td>{{ item.startTime }} - {{ item.endTime }}</td>
            <td>{{ item.reason || '-' }}</td>
            <td><span class="tag">{{ item.status }}</span></td>
            <td class="actions">
              <button class="btn primary" @click="handle(item, 'approve')">通过</button>
              <button class="btn ghost" @click="handle(item, 'reject')">驳回</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无待审批申请</div>
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
