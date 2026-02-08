<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '@/services/api'
import { setToken, setUser } from '@/services/auth'
import MarketingLayout from '@/components/MarketingLayout.vue'

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)
const error = ref('')

const submit = async () => {
  error.value = ''
  if (!form.value.username || !form.value.password) {
    error.value = '请输入账号和密码'
    return
  }
  loading.value = true
  try {
    const data = await request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(form.value)
    })
    setToken(data.token)
    setUser(data.user)
    router.push('/dashboard')
  } catch (e) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <MarketingLayout>
    <div class="login container">
      <div class="login-card card">
        <div>
          <p class="pill">统一登录入口</p>
          <h1>校园综合服务系统</h1>
          <p class="subtle">请使用校内账号登录，管理员可配置规则与资源。</p>
        </div>
        <form class="form-grid" @submit.prevent="submit">
          <label class="field">
            用户名
            <input v-model="form.username" class="input" placeholder="输入账号" />
          </label>
          <label class="field">
            密码
            <input v-model="form.password" class="input" type="password" placeholder="输入密码" />
          </label>
          <p v-if="error" class="error">{{ error }}</p>
          <button class="btn primary block" type="submit" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
          <p class="helper">默认管理员：admin / admin123（首次启动自动创建）</p>
        </form>
      </div>
      <div class="login-side card">
        <h3>安全提示</h3>
        <ul>
          <li>请在校内网络环境使用</li>
          <li>登录后可查看预约与审批进度</li>
          <li>如遇问题请联系系统管理员</li>
        </ul>
      </div>
    </div>
  </MarketingLayout>
</template>

<style scoped>
.login {
  padding: 48px 0 80px;
  display: grid;
  grid-template-columns: 1.05fr 0.65fr;
  gap: 24px;
}

.login-card {
  padding: 28px;
  display: grid;
  gap: 20px;
}

.login-card h1 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 30px;
  margin: 12px 0;
}

.error {
  color: #dc2626;
  font-size: 13px;
}

.login-side {
  padding: 24px;
  display: grid;
  gap: 12px;
  align-content: start;
}

.login-side ul {
  list-style: none;
  display: grid;
  gap: 8px;
  color: var(--text-muted);
}

.login-side li::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  display: inline-block;
  margin-right: 10px;
  transform: translateY(-1px);
}

@media (max-width: 960px) {
  .login {
    grid-template-columns: 1fr;
  }
}
</style>
