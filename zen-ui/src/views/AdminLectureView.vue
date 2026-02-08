<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')
const message = ref('')
const editingId = ref(null)

const form = ref({
  title: '',
  speaker: '',
  location: '',
  startTime: '',
  endTime: '',
  capacity: 50,
  status: 'DRAFT'
})

const normalizeDateTime = (value) => {
  if (!value) return ''
  return `${value}:00`
}

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/admin/lectures')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    title: '',
    speaker: '',
    location: '',
    startTime: '',
    endTime: '',
    capacity: 50,
    status: 'DRAFT'
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.title || !form.value.startTime || !form.value.endTime) {
    error.value = '请完整填写必填信息'
    return
  }
  const payload = {
    title: form.value.title,
    speaker: form.value.speaker,
    location: form.value.location,
    startTime: normalizeDateTime(form.value.startTime),
    endTime: normalizeDateTime(form.value.endTime),
    capacity: Number(form.value.capacity),
    status: form.value.status
  }
  try {
    if (editingId.value) {
      await request(`/api/admin/lectures/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      message.value = '讲座已更新'
    } else {
      await request('/api/admin/lectures', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      message.value = '讲座已创建'
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
    title: item.title,
    speaker: item.speaker || '',
    location: item.location || '',
    startTime: item.startTime ? item.startTime.slice(0, 16) : '',
    endTime: item.endTime ? item.endTime.slice(0, 16) : '',
    capacity: item.capacity,
    status: item.status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="讲座管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑讲座' : '新增讲座' }}</h2>
        <span>发布前设置为 OPEN</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          讲座标题（必填）
          <input v-model="form.title" class="input" placeholder="如 人工智能伦理与治理" />
        </label>
        <label class="field">
          讲者
          <input v-model="form.speaker" class="input" />
        </label>
        <label class="field">
          地点
          <input v-model="form.location" class="input" />
        </label>
        <label class="field">
          开始时间（必填）
          <input v-model="form.startTime" class="input" type="datetime-local" />
        </label>
        <label class="field">
          结束时间（必填）
          <input v-model="form.endTime" class="input" type="datetime-local" />
        </label>
        <label class="field">
          容量
          <input v-model="form.capacity" class="input" type="number" />
        </label>
        <label class="field">
          状态
          <select v-model="form.status" class="select">
            <option value="DRAFT">草稿</option>
            <option value="OPEN">开放报名</option>
            <option value="CLOSED">已关闭</option>
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
            <th>标题</th>
            <th>时间</th>
            <th>地点</th>
            <th>容量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.title }}</td>
            <td>{{ item.startTime }} - {{ item.endTime }}</td>
            <td>{{ item.location || '-' }}</td>
            <td>{{ item.capacity }}</td>
            <td>{{ item.status }}</td>
            <td>
              <button class="btn ghost" @click="edit(item)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无讲座数据</div>
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
