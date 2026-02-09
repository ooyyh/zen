<script setup>
import { ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const form = ref({
  title: '',
  content: '',
  targetRole: 'ALL'
})

const error = ref('')
const message = ref('')

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.title || !form.value.content) {
    error.value = '请填写完整信息'
    return
  }
  try {
    const sent = await request('/api/admin/messages/broadcast', {
      method: 'POST',
      body: JSON.stringify({
        title: form.value.title,
        content: form.value.content,
        targetRole: form.value.targetRole
      })
    })
    message.value = `成功发送 ${sent} 条通知`
    form.value.title = ''
    form.value.content = ''
  } catch (e) {
    error.value = e.message || '发送失败'
  }
}
</script>

<template>
  <AppShell title="通知发布">
    <div class="card form-card">
      <div class="section-title">
        <h2>批量发送通知</h2>
        <span>可向指定角色群发消息</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          通知标题
          <input v-model="form.title" class="input" placeholder="例如：系统维护通知" />
        </label>
        <label class="field">
          通知内容
          <textarea v-model="form.content" class="textarea" placeholder="请输入通知内容..."></textarea>
        </label>
        <label class="field">
          目标角色
          <select v-model="form.targetRole" class="select">
            <option value="ALL">全体用户</option>
            <option value="STUDENT">学生</option>
            <option value="TEACHER">教师</option>
            <option value="ADMIN">管理员</option>
          </select>
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <button class="btn primary" type="submit">立即发送</button>
      </form>
    </div>
  </AppShell>
</template>

<style scoped>
.form-card {
  padding: 20px;
  display: grid;
  gap: 16px;
  max-width: 720px;
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
