<script setup>
import { ref, onMounted } from 'vue'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const classrooms = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const error = ref('')
const message = ref('')

const form = ref({
  classroomId: '',
  startTime: '',
  endTime: '',
  reason: ''
})

const loadClassrooms = async () => {
  loading.value = true
  error.value = ''
  try {
    classrooms.value = await request('/api/classrooms')
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
  if (!form.value.classroomId || !form.value.startTime || !form.value.endTime) {
    error.value = '请完整填写预约信息'
    return
  }
  submitLoading.value = true
  try {
    await request('/api/reservations', {
      method: 'POST',
      body: JSON.stringify({
        classroomId: Number(form.value.classroomId),
        startTime: normalizeDateTime(form.value.startTime),
        endTime: normalizeDateTime(form.value.endTime),
        reason: form.value.reason
      })
    })
    message.value = '预约已提交，请在“我的预约”查看进度'
    form.value = { classroomId: '', startTime: '', endTime: '', reason: '' }
  } catch (e) {
    error.value = e.message || '提交失败'
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadClassrooms)
</script>

<template>
  <AppShell title="发起预约">
    <div class="card form-card">
      <div class="section-title">
        <h2>预约教室</h2>
        <span>规则由管理员统一配置</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          选择教室
          <select v-model="form.classroomId" class="select">
            <option value="">请选择教室</option>
            <option v-for="room in classrooms" :key="room.id" :value="room.id">
              {{ room.building }} {{ room.roomNo }} · 容量 {{ room.capacity }}
            </option>
          </select>
          <span class="helper" v-if="!classrooms.length">暂无教室，请联系管理员录入</span>
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
          预约用途
          <textarea v-model="form.reason" class="textarea" placeholder="如：班会/课程/答辩" />
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <button class="btn primary" type="submit" :disabled="submitLoading">
          {{ submitLoading ? '提交中...' : '提交预约' }}
        </button>
      </form>
    </div>
  </AppShell>
</template>

<style scoped>
.form-card {
  padding: 20px;
  display: grid;
  gap: 16px;
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
