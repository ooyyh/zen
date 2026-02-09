<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '@/services/api'
import { setToken, setUser } from '@/services/auth'
import MarketingLayout from '@/components/MarketingLayout.vue'

const router = useRouter()
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  role: 'STUDENT',
  realName: ''
})
const loading = ref(false)
const error = ref('')

const submit = async () => {
  error.value = ''
  
  if (!form.value.username || !form.value.password) {
    error.value = '请输入用户名和密码'
    return
  }
  
  if (form.value.password.length < 6) {
    error.value = '密码至少需要6位'
    return
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次密码不一致'
    return
  }
  
  loading.value = true
  try {
    const data = await request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        username: form.value.username,
        password: form.value.password,
        role: form.value.role,
        realName: form.value.realName
      })
    })
    setToken(data.token)
    setUser(data.user)
    router.push('/dashboard')
  } catch (e) {
    error.value = e.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <MarketingLayout>
    <div class="register container">
      <div class="register-card card">
        <div>
          <p class="pill">新用户注册</p>
          <h1>校园综合服务系统</h1>
          <p class="subtle">注册新账号以使用校园服务</p>
        </div>
        <form class="form-grid" @submit.prevent="submit">
          <label class="field">
            用户名 *
            <input v-model="form.username" class="input" placeholder="3-50个字符" />
          </label>
          
          <label class="field">
            真实姓名（可选）
            <input v-model="form.realName" class="input" placeholder="输入真实姓名" />
          </label>
          
          <label class="field">
            角色 *
            <select v-model="form.role" class="input">
              <option value="STUDENT">学生</option>
              <option value="TEACHER">教师</option>
            </select>
          </label>
          
          <label class="field">
            密码 *
            <input v-model="form.password" class="input" type="password" placeholder="至少6位字符" />
          </label>
          
          <label class="field">
            确认密码 *
            <input v-model="form.confirmPassword" class="input" type="password" placeholder="再次输入密码" />
          </label>
          
          <p v-if="error" class="error">{{ error }}</p>
          
          <button class="btn primary block" type="submit" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
          </button>
          
          <p class="helper">
            已有账号？
            <RouterLink to="/login" class="link">立即登录</RouterLink>
          </p>
        </form>
      </div>
      
      <div class="register-side card">
        <h3>注册须知</h3>
        <ul>
          <li>学生账号可以预约教室、借用设备、报名讲座</li>
          <li>教师账号拥有管理讲座、审批设备等额外权限</li>
          <li>请使用真实信息注册</li>
          <li>如需特殊权限请联系管理员</li>
        </ul>
      </div>
    </div>
  </MarketingLayout>
</template>

<style scoped>
.register {
  padding: 48px 0 80px;
  display: grid;
  grid-template-columns: 1.05fr 0.65fr;
  gap: 24px;
}

.register-card {
  padding: 28px;
  display: grid;
  gap: 20px;
}

.register-card h1 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 30px;
  margin: 12px 0;
}

.error {
  color: #dc2626;
  font-size: 13px;
}

.register-side {
  padding: 24px;
  display: grid;
  gap: 12px;
  align-content: start;
}

.register-side ul {
  list-style: none;
  display: grid;
  gap: 8px;
  color: var(--text-muted);
}

.register-side li::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  display: inline-block;
  margin-right: 10px;
  transform: translateY(-1px);
}

.link {
  color: var(--accent);
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 960px) {
  .register {
    grid-template-columns: 1fr;
  }
}
</style>
