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
  templateCode: '',
  title: '',
  content: ''
})

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    list.value = await request('/api/admin/message-templates')
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const reset = () => {
  editingId.value = null
  form.value = {
    templateCode: '',
    title: '',
    content: ''
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.title || !form.value.content || (!editingId.value && !form.value.templateCode)) {
    error.value = '请填写完整信息'
    return
  }
  try {
    if (editingId.value) {
      await request(`/api/admin/message-templates/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify({
          title: form.value.title,
          content: form.value.content
        })
      })
      message.value = '更新成功'
    } else {
      await request('/api/admin/message-templates', {
        method: 'POST',
        body: JSON.stringify({
          templateCode: form.value.templateCode,
          title: form.value.title,
          content: form.value.content
        })
      })
      message.value = '创建成功'
    }
    reset()
    await load()
  } catch (e) {
    error.value = e.message || '操作失败'
  }
}

const edit = (item) => {
  editingId.value = item.id
  form.value = {
    templateCode: item.templateCode,
    title: item.title,
    content: item.content
  }
}

const short = (value) => {
  if (!value) return '-'
  return value.length > 30 ? `${value.slice(0, 30)}...` : value
}

onMounted(load)
</script>

<template>
  <AppShell title="消息模板">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑模板' : '新建模板' }}</h2>
        <span>用于系统自动发送通知</span>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          模板编码（唯一）
          <input v-model="form.templateCode" class="input" :disabled="!!editingId" placeholder="例如：RESERVATION_APPROVED" />
        </label>
        <label class="field">
          消息标题
          <input v-model="form.title" class="input" placeholder="例如：预约审批通过" />
        </label>
        <label class="field">
          消息内容
          <textarea v-model="form.content" class="textarea" placeholder="例如：您预约的 {classroom} 在 {time} 已通过审批"></textarea>
        </label>
        <p class="helper">使用花括号定义变量，如：{name}</p>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <div class="actions">
          <button class="btn primary" type="submit">保存模板</button>
          <button class="btn ghost" type="button" @click="reset">取消</button>
        </div>
      </form>
    </div>

    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>模板编码</th>
            <th>标题</th>
            <th>内容预览</th>
            <th>更新时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.templateCode }}</td>
            <td>{{ item.title }}</td>
            <td>{{ short(item.content) }}</td>
            <td>{{ item.updatedAt || '-' }}</td>
            <td>
              <button class="btn ghost" @click="edit(item)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length && !loading" class="empty">暂无模板</div>
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
