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
  category: '',
  assetNo: '',
  location: '',
  totalQty: 1,
  status: 1
})

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/admin/equipments')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const resetForm = () => {
  editingId.value = null
  form.value = {
    name: '',
    category: '',
    assetNo: '',
    location: '',
    totalQty: 1,
    status: 1
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.name || !form.value.totalQty) {
    error.value = '请完整填写必填信息'
    return
  }
  const payload = {
    name: form.value.name,
    category: form.value.category,
    assetNo: form.value.assetNo,
    location: form.value.location,
    totalQty: Number(form.value.totalQty),
    status: Number(form.value.status)
  }
  try {
    if (editingId.value) {
      await request(`/api/admin/equipments/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      message.value = '设备已更新'
    } else {
      await request('/api/admin/equipments', {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      message.value = '设备已创建'
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
    category: item.category || '',
    assetNo: item.assetNo || '',
    location: item.location || '',
    totalQty: item.totalQty,
    status: item.status
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="设备管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑设备' : '新增设备' }}</h2>
        <span>维护资产信息</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          设备名称（必填）
          <input v-model="form.name" class="input" placeholder="如 单反相机" />
        </label>
        <label class="field">
          类别
          <input v-model="form.category" class="input" placeholder="如 摄影设备" />
        </label>
        <label class="field">
          资产编号
          <input v-model="form.assetNo" class="input" placeholder="如 CAM-001" />
        </label>
        <label class="field">
          存放位置
          <input v-model="form.location" class="input" placeholder="如 实验楼 2F" />
        </label>
        <label class="field">
          数量
          <input v-model="form.totalQty" class="input" type="number" min="1" />
        </label>
        <label class="field">
          状态
          <select v-model="form.status" class="select">
            <option :value="1">可借用</option>
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
            <th>名称</th>
            <th>类别</th>
            <th>资产编号</th>
            <th>位置</th>
            <th>数量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.name }}</td>
            <td>{{ item.category || '-' }}</td>
            <td>{{ item.assetNo || '-' }}</td>
            <td>{{ item.location || '-' }}</td>
            <td>{{ item.totalQty }}</td>
            <td>{{ item.status === 1 ? '可借用' : '停用' }}</td>
            <td>
              <button class="btn ghost" @click="edit(item)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无设备数据</div>
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
