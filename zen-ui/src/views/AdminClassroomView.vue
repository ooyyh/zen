<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const loading = ref(false)
const error = ref('')
const message = ref('')
const editingId = ref(null)

const form = ref({
  building: '',
  roomNo: '',
  capacity: '',
  location: '',
  equipmentJson: '',
  status: 1
})

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    list.value = await request('/api/classrooms')
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    building: '',
    roomNo: '',
    capacity: '',
    location: '',
    equipmentJson: '',
    status: 1
  }
}

const submit = async () => {
  message.value = ''
  error.value = ''
  if (!form.value.building || !form.value.roomNo || !form.value.capacity) {
    error.value = '请完整填写必填信息'
    return
  }
  const payload = {
    ...form.value,
    capacity: Number(form.value.capacity)
  }
  try {
    if (editingId.value) {
      await request(`/api/admin/classrooms/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      message.value = '教室已更新'
    } else {
      await request('/api/admin/classrooms', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      message.value = '教室已创建'
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
    building: item.building,
    roomNo: item.roomNo,
    capacity: item.capacity,
    location: item.location || '',
    equipmentJson: item.equipmentJson || '',
    status: item.status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="教室管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑教室' : '新增教室' }}</h2>
        <span>可配置容量、位置与设备</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          楼栋（必填）
          <input v-model="form.building" class="input" placeholder="例如 A 区" />
        </label>
        <label class="field">
          教室号（必填）
          <input v-model="form.roomNo" class="input" placeholder="例如 101" />
        </label>
        <label class="field">
          容量（必填）
          <input v-model="form.capacity" class="input" type="number" placeholder="例如 60" />
        </label>
        <label class="field">
          位置描述
          <input v-model="form.location" class="input" placeholder="如 理科楼 2F" />
        </label>
        <label class="field">
          设备配置
          <input v-model="form.equipmentJson" class="input" placeholder="如 投影/录播/音响" />
        </label>
        <label class="field">
          状态
          <select v-model="form.status" class="select">
            <option :value="1">可用</option>
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
            <th>楼栋</th>
            <th>教室号</th>
            <th>容量</th>
            <th>位置</th>
            <th>设备</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.building }}</td>
            <td>{{ item.roomNo }}</td>
            <td>{{ item.capacity }}</td>
            <td>{{ item.location || '-' }}</td>
            <td>{{ item.equipmentJson || '-' }}</td>
            <td>{{ item.status === 1 ? '可用' : '停用' }}</td>
            <td>
              <button class="btn ghost" @click="edit(item)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length && !loading" class="empty">暂无教室数据</div>
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
