<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')
const message = ref('')
const editingId = ref(null)

const form = ref({
  name: '',
  origin: '',
  destination: '',
  status: 1
})

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/admin/bus/routes')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    name: '',
    origin: '',
    destination: '',
    status: 1
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.name || !form.value.origin || !form.value.destination) {
    error.value = '请完整填写必填信息'
    return
  }
  const payload = { ...form.value }
  try {
    if (editingId.value) {
      await request(`/api/admin/bus/routes/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      message.value = '线路已更新'
    } else {
      await request('/api/admin/bus/routes', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      message.value = '线路已创建'
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
    name: item.name,
    origin: item.origin,
    destination: item.destination,
    status: item.status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="线路管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑线路' : '新增线路' }}</h2>
        <span>用于校车班次配置</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          线路名称（必填）
          <input v-model="form.name" class="input" placeholder="如 东校区-南校区" />
        </label>
        <label class="field">
          起点（必填）
          <input v-model="form.origin" class="input" />
        </label>
        <label class="field">
          终点（必填）
          <input v-model="form.destination" class="input" />
        </label>
        <label class="field">
          状态
          <select v-model="form.status" class="select">
            <option :value="1">启用</option>
            <option :value="0">停用</option>
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
            <th>起点</th>
            <th>终点</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.name }}</td>
            <td>{{ item.origin }}</td>
            <td>{{ item.destination }}</td>
            <td>{{ item.status === 1 ? '启用' : '停用' }}</td>
            <td>
              <button class="btn ghost" @click="edit(item)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无线路数据</div>
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
