<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '@/components/AppShell.vue'
import { request } from '@/services/api'

const router = useRouter()
const list = ref([])
const error = ref('')
const message = ref('')
const editingId = ref(null)

const form = ref({
  name: '',
  building: '',
  floor: null,
  area: '',
  totalSeats: null
})

const load = async () => {
  error.value = ''
  try {
    list.value = await request('/api/admin/study-rooms')
  } catch (e) {
    error.value = e.message || '加载失败'
  }
}

const reset = () => {
  editingId.value = null
  form.value = {
    name: '',
    building: '',
    floor: null,
    area: '',
    totalSeats: null
  }
}

const submit = async () => {
  error.value = ''
  message.value = ''
  if (!form.value.name || !form.value.building || !form.value.floor || !form.value.totalSeats) {
    error.value = '请填写完整信息'
    return
  }
  try {
    if (editingId.value) {
      await request(`/api/admin/study-rooms/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify(form.value)
      })
      message.value = '更新成功'
    } else {
      await request('/api/admin/study-rooms', {
        method: 'POST',
        body: JSON.stringify(form.value)
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
    name: item.name,
    building: item.building,
    floor: item.floor,
    area: item.area,
    totalSeats: item.totalSeats
  }
}

const remove = async (id) => {
  if (!confirm('确认删除该自习室？')) return
  try {
    await request(`/api/admin/study-rooms/${id}`, { method: 'DELETE' })
    await load()
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

const manageSeats = (room) => {
  router.push(`/admin/study-rooms/${room.id}/seats`)
}

onMounted(load)
</script>

<template>
  <AppShell title="自习室管理">
    <div class="card form-card">
      <div class="section-title">
        <h2>{{ editingId ? '编辑自习室' : '新建自习室' }}</h2>
      </div>
      <form class="form-grid" @submit.prevent="submit">
        <label class="field">
          自习室名称
          <input v-model="form.name" class="input" placeholder="例如：第一自习室" />
        </label>
        <label class="field">
          所在楼栋
          <input v-model="form.building" class="input" placeholder="例如：图书馆" />
        </label>
        <label class="field">
          楼层
          <input v-model.number="form.floor" type="number" class="input" placeholder="例如：3" />
        </label>
        <label class="field">
          区域（可选）
          <input v-model="form.area" class="input" placeholder="例如：东区" />
        </label>
        <label class="field">
          座位总数
          <input v-model.number="form.totalSeats" type="number" class="input" placeholder="例如：100" />
        </label>
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="message" class="success">{{ message }}</p>
        <div class="actions">
          <button class="btn primary" type="submit">保存</button>
          <button class="btn ghost" type="button" @click="reset">取消</button>
        </div>
      </form>
    </div>

    <div class="card table-card">
      <table class="table">
        <thead>
          <tr>
            <th>名称</th>
            <th>楼栋</th>
            <th>楼层</th>
            <th>区域</th>
            <th>座位数</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody v-if="list.length">
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.name }}</td>
            <td>{{ item.building }}</td>
            <td>{{ item.floor }}F</td>
            <td>{{ item.area || '-' }}</td>
            <td>{{ item.totalSeats }}</td>
            <td class="actions">
              <button class="btn ghost" @click="edit(item)">编辑</button>
              <button class="btn ghost" @click="manageSeats(item)">座位管理</button>
              <button class="btn ghost" @click="remove(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!list.length" class="empty">暂无自习室</div>
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

.form-grid {
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

.table .actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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
