<script setup>
import MarketingLayout from '@/components/MarketingLayout.vue'
</script>

<template>
  <MarketingLayout>
    <div class="about container">
    <div class="about-hero card">
      <div>
        <p class="pill">平台介绍</p>
        <h1>校园综合服务系统</h1>
        <p class="subtle">
          Zen Campus 聚焦“预约、审批、通知、统计”全流程，统一管理教室、讲座、设备与校车等资源，
          通过可视化看板与并发保障能力，让师生办事更高效、管理更透明。
        </p>
      </div>
      <div class="about-metrics">
        <div>
          <p class="metric-value">8</p>
          <p class="metric-label">核心业务模块</p>
        </div>
        <div>
          <p class="metric-value">16</p>
          <p class="metric-label">协同部门</p>
        </div>
        <div>
          <p class="metric-value">24h</p>
          <p class="metric-label">消息触达</p>
        </div>
      </div>
    </div>

    <div class="about-grid section">
      <div class="card about-card">
        <h3>系统能力</h3>
        <ul>
          <li>分布式锁 + 限流策略，保障高峰期稳定性</li>
          <li>预约热度预测，智能推荐可用资源</li>
          <li>审批链可视化，跨部门进度透明</li>
          <li>通知中心聚合，异步提醒不遗漏</li>
        </ul>
      </div>
      <div class="card about-card">
        <h3>服务对象</h3>
        <ul>
          <li>学生：教室、讲座、设备借用与活动申请</li>
          <li>教师：科研场地预约、课程调度、学术活动</li>
          <li>管理部门：资源审批、统计与运维保障</li>
          <li>后勤支持：校车排班、设备维护、场地巡检</li>
        </ul>
      </div>
    </div>
    </div>
  </MarketingLayout>
</template>

<style scoped>
.about {
  padding: 40px 0 80px;
}

.about-hero {
  padding: 28px;
  display: grid;
  gap: 24px;
}

.about-hero h1 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 32px;
  margin: 12px 0;
}

.about-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
}

.metric-label {
  font-size: 13px;
  color: var(--text-muted);
}

.about-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.about-card {
  padding: 20px;
}

.about-card h3 {
  font-size: 18px;
  margin-bottom: 12px;
}

.about-card ul {
  list-style: none;
  display: grid;
  gap: 10px;
  color: var(--text-muted);
}

.about-card li::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--accent);
  margin-right: 10px;
  transform: translateY(-1px);
}

@media (max-width: 960px) {
  .about-grid {
    grid-template-columns: 1fr;
  }

  .about-metrics {
    grid-template-columns: 1fr;
  }
}
</style>
