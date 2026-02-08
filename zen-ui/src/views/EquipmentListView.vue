<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const loading = ref(false)
const error = ref('')
const message = ref('')

const form = ref({
  equipmentId: '',
  startTime: '',
  endTime: '',
  reason: ''
})

const query = ref({
  keyword: '',
  category: ''
})

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const params = new URLSearchParams()
    if (query.value.category) params.append('category', query.value.category)
    if (query.value.keyword) params.append('keyword', query.value.keyword)
    const data = await request(`/api/equipments?${params.toString()}`)
    list.value = data
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const normalizeDateTime = (value) => {
  if (!value) return ''
  return `${value}:00`
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.equipmentId || !form.value.startTime || !form.value.endTime) {
    error.value = '请完整填写借用信息'
    return
  }
  try {
    await request(`/api/equipments/${form.value.equipmentId}/borrow`, {
      method: 'POST',
      body: JSON.stringify({
        startTime: normalizeDateTime(form.value.startTime),
        endTime: normalizeDateTime(form.value.endTime),
        reason: form.value.reason
      })
    })
    message.value = '借用申请已提交，请等待审批'
    form.value = { equipmentId: '', startTime: '', endTime: '', reason: '' }
  } catch (e) {
    error.value = e.message || '提交失败'
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="设备借用">
    <div class="card filter-card">
      <div class="filter-grid">
        <label class="field">
          类别
          <input v-model="query.category" class="input" placeholder="如 摄影" />
        </label>
        <label class="field">
          关键词
          <input v-model="query.keyword" class="input" placeholder="设备名称/资产编号" />
        </label>
        <div class="filter-actions">
          <button class="btn primary" @click="load">筛选</button>
          <button class="btn ghost" @click="query = { keyword: '', category: '' }; load()">重置</button>
        </div>
      </div>
    </div>

    <div class="card form-card">
      <div class="section-title">
        <h2>提交借用申请</h2>
        <span>需管理员审批</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          选择设备
          <select v-model="form.equipmentId" class="select">
            <option value="">请选择设备</option>
            <option v-for="item in list" :key="item.id" :value="item.id">
              {{ item.name }} · {{ item.assetNo || '无编号' }} · 数量 {{ item.totalQty }}
            </option>
          </select>
        </label>
        <label class="field">
          开始时间
          <input v-model="form.startTime" class="input" type="datetime-local" />
        </label>
        <label class="field">
          结束时间
          <input v-model="form.endTime" class="input" type="datetime-local" />
        </label>
        <label class="field">
          用途说明
          <textarea v-model="form.reason" class="textarea" placeholder="如 课程拍摄/活动直播" />
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <button class="btn primary" type="submit">提交申请</button>
      </form>
    </div>

    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>名称</th>
            <th>类别</th>
            <th>资产编号</th>
            <th>位置</th>
            <th>数量</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.name }}</td>
            <td>{{ item.category || '-' }}</td>
            <td>{{ item.assetNo || '-' }}</td>
            <td>{{ item.location || '-' }}</td>
            <td>{{ item.totalQty }}</td>
            <td>
              <span class="tag" v-if="item.status === 1">可借用</span>
              <span class="tag" v-else>停用</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length && !loading" class="empty">暂无设备数据</div>
    </div>
  </AppShell>
</template>

<style scoped>
.filter-card {
  padding: 16px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  align-items: end;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.form-card {
  padding: 20px;
  display: grid;
  gap: 16px;
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

@media (max-width: 900px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
}
</style>
