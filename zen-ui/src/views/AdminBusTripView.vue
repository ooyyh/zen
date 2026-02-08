<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const routes = ref([])
const list = ref([])
const error = ref('')
const message = ref('')
const editingId = ref(null)

const form = ref({
  routeId: '',
  busNo: '',
  departureTime: '',
  arrivalTime: '',
  capacity: 40,
  status: 'OPEN'
})

const normalizeDateTime = (value) => {
  if (!value) return ''
  return `${value}:00`
}

const load = async () => {
  error.value = ''
  try {
    const [routeList, tripList] = await Promise.all([
      request('/api/admin/bus/routes'),
      request('/api/admin/bus/trips')
    ])
    routes.value = routeList
    list.value = tripList
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    routeId: '',
    busNo: '',
    departureTime: '',
    arrivalTime: '',
    capacity: 40,
    status: 'OPEN'
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.routeId || !form.value.departureTime || !form.value.arrivalTime) {
    error.value = '请完整填写必填信息'
    return
  }
  const payload = {
    routeId: Number(form.value.routeId),
    busNo: form.value.busNo,
    departureTime: normalizeDateTime(form.value.departureTime),
    arrivalTime: normalizeDateTime(form.value.arrivalTime),
    capacity: Number(form.value.capacity),
    status: form.value.status
  }
  try {
    if (editingId.value) {
      await request(`/api/admin/bus/trips/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      message.value = '班次已更新'
    } else {
      await request('/api/admin/bus/trips', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      message.value = '班次已创建'
    }
    resetForm()
    await load()
  } catch (e) {
    error.value = e.message || '保存失败'
  }
}

const edit = (item) => {
  editingId.value = item.id
  form.value = {
    routeId: item.routeId,
    busNo: item.busNo || '',
    departureTime: item.departureTime ? item.departureTime.slice(0, 16) : '',
    arrivalTime: item.arrivalTime ? item.arrivalTime.slice(0, 16) : '',
    capacity: item.capacity,
    status: item.status
  }
}

const routeName = (routeId) => {
  const route = routes.value.find((r) => r.id === routeId)
  if (!route) return '-'
  return `${route.origin} → ${route.destination}`
}

onMounted(load)
</script>

<template>
  <AppShell title="班次管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑班次' : '新增班次' }}</h2>
        <span>开放预约需设置为 OPEN</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          线路（必填）
          <select v-model="form.routeId" class="select">
            <option value="">请选择线路</option>
            <option v-for="route in routes" :key="route.id" :value="route.id">
              {{ route.origin }} → {{ route.destination }}
            </option>
          </select>
        </label>
        <label class="field">
          车次编号
          <input v-model="form.busNo" class="input" placeholder="如 A-01" />
        </label>
        <label class="field">
          发车时间（必填）
          <input v-model="form.departureTime" class="input" type="datetime-local" />
        </label>
        <label class="field">
          到达时间（必填）
          <input v-model="form.arrivalTime" class="input" type="datetime-local" />
        </label>
        <label class="field">
          座位数
          <input v-model="form.capacity" class="input" type="number" min="1" />
        </label>
        <label class="field">
          状态
          <select v-model="form.status" class="select">
            <option value="OPEN">开放</option>
            <option value="CLOSED">关闭</option>
          </select>
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <div class="actions">
          <button class="btn primary" type="submit">保存</button>
          <button class="btn ghost" type="button" @click="resetForm">清空</button>
        </div>
      </form>
    </div>

    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>线路</th>
            <th>时间</th>
            <th>车次</th>
            <th>座位</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ routeName(item.routeId) }}</td>
            <td>{{ item.departureTime }} - {{ item.arrivalTime }}</td>
            <td>{{ item.busNo || '-' }}</td>
            <td>{{ item.capacity }}</td>
            <td>{{ item.status }}</td>
            <td>
              <button class="btn ghost" @click="edit(item)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无班次数据</div>
    </div>
  </AppShell>
</template>

<style scoped>
.form-card {
  padding: 20px;
  display: grid;
  gap: 16px;
}

.actions {
  display: flex;
  gap: 10px;
}

.table-card {
  padding: 10px 16px 18px;
}

.error {
  color: #dc2626;
  font-size: 13px;
}

.success {
  color: #16a34a;
  font-size: 13px;
}
</style>
