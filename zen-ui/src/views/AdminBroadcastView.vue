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
    error.value = '?????????'
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
    message.value = `???? ${sent} ?`
    form.value.title = ''
    form.value.content = ''
  } catch (e) {
    error.value = e.message || '????'
  }
}
</script>

<template>
  <AppShell title="????">
    <div class="card form-card">
      <div class="section-title">
        <h2>??????</h2>
        <span>?????????</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          ????
          <input v-model="form.title" class="input" placeholder="?? ??????" />
        </label>
        <label class="field">
          ????
          <textarea v-model="form.content" class="textarea" placeholder="??????..."></textarea>
        </label>
        <label class="field">
          ????
          <select v-model="form.targetRole" class="select">
            <option value="ALL">????</option>
            <option value="STUDENT">??</option>
            <option value="TEACHER">??</option>
            <option value="ADMIN">???</option>
          </select>
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <button class="btn primary" type="submit">????</button>
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
