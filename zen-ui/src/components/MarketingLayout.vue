<script setup>
import { RouterLink, useRouter } from 'vue-router'
import { clearAuth, getUser } from '@/services/auth'

const router = useRouter()
const user = getUser()

const logout = () => {
  clearAuth()
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="container topbar-inner">
        <RouterLink to="/" class="brand">
          <div class="brand-mark">
            <span class="brand-dot"></span>
          </div>
          <div>
            <p class="brand-title">Zen Campus</p>
            <p class="brand-sub">校园综合服务平台</p>
          </div>
        </RouterLink>

        <nav class="nav">
          <RouterLink to="/">服务首页</RouterLink>
          <RouterLink to="/about">平台介绍</RouterLink>
          <RouterLink to="/dashboard">服务控制台</RouterLink>
          <RouterLink to="/classrooms">教室资源</RouterLink>
        </nav>

        <div class="topbar-actions">
          <template v-if="user">
            <div class="user-chip">
              <span>{{ user.username }}</span>
              <em>{{ user.role }}</em>
            </div>
            <button class="btn ghost" @click="logout">退出登录</button>
          </template>
          <template v-else>
            <RouterLink class="btn ghost" to="/login">教师/学生登录</RouterLink>
            <RouterLink class="btn primary" to="/login">进入系统</RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main>
      <slot />
    </main>

    <footer class="footer">
      <div class="container footer-inner">
        <div>
          <p class="footer-title">Zen Campus · 校园综合服务系统</p>
          <p class="subtle">预约、审批、通知一体化，面向师生与管理部门。</p>
        </div>
        <div class="footer-meta">
          <span>服务时间 07:00-23:00</span>
          <span>运维热线 027-8888-6677</span>
          <span>系统版本 v1.0.0</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 30;
  backdrop-filter: blur(16px);
  background: rgba(245, 247, 251, 0.88);
  border-bottom: 1px solid var(--border);
}

.topbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 16px 0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(135deg, #1d4ed8, #0ea5e9);
  display: grid;
  place-items: center;
  box-shadow: var(--shadow-sm);
}

.brand-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #f8fafc;
  box-shadow: inset 0 0 0 3px rgba(15, 23, 42, 0.08);
}

.brand-title {
  font-weight: 600;
  letter-spacing: 0.2px;
}

.brand-sub {
  font-size: 12px;
  color: var(--text-muted);
}

.nav {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 14px;
  color: var(--text-muted);
}

.nav a {
  padding: 6px 10px;
  border-radius: 999px;
  transition: color 0.2s ease, background-color 0.2s ease;
}

.nav a.router-link-exact-active,
.nav a:hover {
  color: var(--primary);
  background: rgba(29, 78, 216, 0.1);
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.06);
  font-size: 13px;
}

.user-chip em {
  font-style: normal;
  color: var(--text-muted);
}

.footer {
  margin-top: auto;
  border-top: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.7);
}

.footer-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 0 36px;
  font-size: 14px;
}

.footer-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.footer-meta {
  display: flex;
  gap: 16px;
  color: var(--text-muted);
}

@media (max-width: 960px) {
  .topbar-inner {
    flex-wrap: wrap;
  }

  .nav {
    order: 3;
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .footer-inner {
    flex-direction: column;
    align-items: flex-start;
  }

  .footer-meta {
    flex-wrap: wrap;
  }
}

@media (max-width: 720px) {
  .topbar-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
