<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const list = ref([])
const error = ref('')
const message = ref('')

const form = ref({
  username: '',
  password: '',
  role: 'STUDENT'
})

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/admin/users')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.username || !form.value.password) {
    error.value = '请完整填写用户信息'
    return
  }
  try {
    await request('/api/admin/users', {
      method: 'POST',
      body: JSON.stringify(form.value)
    })
    message.value = '用户已创建'
    form.value = { username: '', password: '', role: 'STUDENT' }
    await load()
  } catch (e) {
    error.value = e.message || '创建失败'
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="用户管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>新增用户</h2>
        <span>支持学生/教师/管理员</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          用户名
          <input v-model="form.username" class="input" placeholder="如 student01" />
        </label>
        <label class="field">
          初始密码
          <input v-model="form.password" class="input" type="password" />
        </label>
        <label class="field">
          角色
          <select v-model="form.role" class="select">
            <option value="STUDENT">学生</option>
            <option value="TEACHER">教师</option>
            <option value="ADMIN">管理员</option>
          </select>
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <button class="btn primary" type="submit">创建用户</button>
      </form>
    </div>

    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.role }}</td>
            <td>{{ item.status === 1 ? '启用' : '禁用' }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无用户数据</div>
    </div>
  </AppShell>
</template>

<style scoped>
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
</style>
