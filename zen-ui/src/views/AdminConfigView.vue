<script setup>
import { onMounted, ref } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const form = ref({
  timeSlotMinutes: 30,
  advanceDays: 7,
  dailyLimit: 2,
  minDurationMinutes: 30,
  maxDurationMinutes: 180,
  approvalRequired: true
})
const error = ref('')
const message = ref('')

const load = async () => {
  error.value = ''
  try {
    form.value = await request('/api/admin/config/reservation-rules')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const save = async () => {
  error.value = ''
  message.value = ''
  try {
    await request('/api/admin/config/reservation-rules', {
      method: 'PUT',
      body: JSON.stringify({
        ...form.value,
        timeSlotMinutes: Number(form.value.timeSlotMinutes),
        advanceDays: Number(form.value.advanceDays),
        dailyLimit: Number(form.value.dailyLimit),
        minDurationMinutes: Number(form.value.minDurationMinutes),
        maxDurationMinutes: Number(form.value.maxDurationMinutes)
      })
    })
    message.value = '规则已更新'
  } catch (e) {
    error.value = e.message || '保存失败'
  }
}

onMounted(load)
</script>

<template>
  <AppShell title="规则配置">
    <div class="card form-card">
      <div class="section-title">
        <h2>预约规则</h2>
        <span>即时生效</span>
      </div>
      <form class="form-grid" @submit.prevent="save">
        <label class="field">
          时间粒度（分钟）
          <input v-model="form.timeSlotMinutes" class="input" type="number" />
        </label>
        <label class="field">
          可提前预约天数
          <input v-model="form.advanceDays" class="input" type="number" />
        </label>
        <label class="field">
          每人每日预约上限
          <input v-model="form.dailyLimit" class="input" type="number" />
        </label>
        <label class="field">
          最小时长（分钟）
          <input v-model="form.minDurationMinutes" class="input" type="number" />
        </label>
        <label class="field">
          最长时长（分钟）
          <input v-model="form.maxDurationMinutes" class="input" type="number" />
        </label>
        <label class="field">
          是否需要审批
          <select v-model="form.approvalRequired" class="select">
            <option :value="true">需要审批</option>
            <option :value="false">无需审批</option>
          </select>
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <button class="btn primary" type="submit">保存规则</button>
      </form>
    </div>
  </AppShell>
</template>

<style scoped>
.form-card {
  padding: 20px;
  display: grid;
  gap: 16px;
  max-width: 620px;
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
