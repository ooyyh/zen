<script setup>
const services = [
  {
    title: '教室预约',
    desc: '按楼栋、容量、设备筛选，秒级反馈空闲时段',
    tag: '实时空闲',
    icon: 'M4 4h16v4H4V4zm0 6h16v10H4V10zm4 2v6h2v-6H8zm6 0v6h2v-6h-2z'
  },
  {
    title: '讲座报名',
    desc: '查看讲座热度与报名进度，自动加入候补队列',
    tag: '智能候补',
    icon: 'M4 6h16v8H4V6zm3-3h10v2H7V3zm-1 13h12v2H6v-2z'
  },
  {
    title: '设备借用',
    desc: '摄影器材、实验仪器、会议设备一键借还',
    tag: '校内资源',
    icon: 'M7 4h10l2 4v9a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V8l2-4zm1 6h8v2H8v-2z'
  },
  {
    title: '校车预约',
    desc: '多校区班次与余位可视化，支持临时加班次',
    tag: '出行保障',
    icon: 'M5 6h14l1.5 4.5V16a2 2 0 0 1-2 2H17a2 2 0 0 1-2-2H9a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-5.5L5 6zm1 5h12v3H6v-3z'
  },
  {
    title: '场地审批',
    desc: '活动申请联动审批流，材料缺失自动提醒',
    tag: '跨部门协同',
    icon: 'M6 4h9l3 3v11a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2zm0 6h12v2H6v-2zm0 4h12v2H6v-2z'
  },
  {
    title: '通知中心',
    desc: '办事进度、提醒、消息分类聚合',
    tag: '多端同步',
    icon: 'M12 4a5 5 0 0 1 5 5v4l2 2H5l2-2V9a5 5 0 0 1 5-5zm-3 14h6a3 3 0 0 1-6 0z'
  }
]

const lectures = [
  {
    title: '人工智能伦理与校园治理',
    room: '逸夫楼 210',
    time: '周三 14:00-16:00',
    tags: '人文讲堂'
  },
  {
    title: '高校科研数据合规管理',
    room: '信息中心 3F',
    time: '周四 19:00-20:30',
    tags: '科研支持'
  },
  {
    title: '科研成果转化路径分享',
    room: '创新中心 401',
    time: '周五 15:00-17:00',
    tags: '产学研'
  }
]

const highlights = [
  {
    title: '分布式锁防冲突',
    desc: '高并发预约场景自动加锁，保障名额公平可控'
  },
  {
    title: '预约热度预测',
    desc: '结合历史数据生成峰值预警与增量场地推荐'
  },
  {
    title: '审批链可视化',
    desc: '不同部门节点与耗时一目了然，支持催办提醒'
  },
  {
    title: '资源画像看板',
    desc: '各楼栋利用率、讲座出勤率、设备周转率可追踪'
  }
]
</script>

<template>
  <div class="page">
    <section class="hero container">
      <div class="hero-copy">
        <span class="pill">综合服务平台 · 2026 春季版</span>
        <h1>让校园预约更快、更稳、更可视</h1>
        <p class="subtle">
          从教室、讲座到设备与校车，统一入口、统一审核、统一消息，解决“多系统、多规则、多等待”。
        </p>
        <div class="hero-actions">
          <button class="btn primary">立即预约</button>
          <button class="btn ghost">查看审批流</button>
        </div>
        <div class="hero-metrics">
          <div>
            <p class="metric-value">1.8 万</p>
            <p class="metric-label">本周可预约时段</p>
          </div>
          <div>
            <p class="metric-value">97%</p>
            <p class="metric-label">高峰成功率</p>
          </div>
          <div>
            <p class="metric-value">42 秒</p>
            <p class="metric-label">平均审批响应</p>
          </div>
        </div>
      </div>

      <div class="hero-panel card">
        <div class="panel-header">
          <div>
            <h3>今日热门场地</h3>
            <p class="subtle">可预约时段实时更新</p>
          </div>
          <button class="btn ghost small">刷新</button>
        </div>
        <div class="panel-list">
          <div class="panel-row">
            <div>
              <p class="row-title">理科楼 A102</p>
              <p class="row-sub">可容纳 120 人 · 投影/录播</p>
            </div>
            <span class="row-tag">余 6</span>
          </div>
          <div class="panel-row">
            <div>
              <p class="row-title">图书馆多媒体室</p>
              <p class="row-sub">可容纳 60 人 · 讨论模式</p>
            </div>
            <span class="row-tag">余 3</span>
          </div>
          <div class="panel-row">
            <div>
              <p class="row-title">创新中心 3F</p>
              <p class="row-sub">可容纳 80 人 · 会议系统</p>
            </div>
            <span class="row-tag">余 9</span>
          </div>
        </div>
        <div class="panel-footer">
          <div>
            <p class="row-title">系统状态</p>
            <p class="row-sub">分布式锁已启用 · 峰值保护运行中</p>
          </div>
          <span class="status-pill">稳定</span>
        </div>
      </div>
    </section>

    <section id="services" class="container section">
      <div class="section-title">
        <h2>服务入口</h2>
        <span>聚合 6 类高频业务</span>
      </div>
      <div class="service-grid">
        <article v-for="service in services" :key="service.title" class="service-card card">
          <div class="service-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path :d="service.icon" fill="currentColor" />
            </svg>
          </div>
          <div>
            <div class="service-header">
              <h3>{{ service.title }}</h3>
              <span class="service-tag">{{ service.tag }}</span>
            </div>
            <p class="subtle">{{ service.desc }}</p>
          </div>
        </article>
      </div>
    </section>

    <section id="lectures" class="container section split">
      <div class="card lecture-card">
        <div class="section-title">
          <h2>本周热门讲座</h2>
          <span>报名人数实时同步</span>
        </div>
        <div class="lecture-list">
          <div v-for="lecture in lectures" :key="lecture.title" class="lecture-row">
            <div>
              <p class="row-title">{{ lecture.title }}</p>
              <p class="row-sub">{{ lecture.room }} · {{ lecture.time }}</p>
            </div>
            <span class="lecture-tag">{{ lecture.tags }}</span>
          </div>
        </div>
        <button class="btn ghost full">查看全部讲座</button>
      </div>

      <div class="card occupancy-card">
        <div class="section-title">
          <h2>场地实时占用</h2>
          <span>07:00-23:00</span>
        </div>
        <div class="occupancy-item">
          <div class="occupancy-header">
            <p>教学楼 A 区</p>
            <span>78%</span>
          </div>
          <div class="bar">
            <span style="width: 78%"></span>
          </div>
        </div>
        <div class="occupancy-item">
          <div class="occupancy-header">
            <p>图书馆</p>
            <span>62%</span>
          </div>
          <div class="bar">
            <span style="width: 62%"></span>
          </div>
        </div>
        <div class="occupancy-item">
          <div class="occupancy-header">
            <p>创新中心</p>
            <span>54%</span>
          </div>
          <div class="bar">
            <span style="width: 54%"></span>
          </div>
        </div>
        <div class="occupancy-item">
          <div class="occupancy-header">
            <p>体育馆</p>
            <span>41%</span>
          </div>
          <div class="bar">
            <span style="width: 41%"></span>
          </div>
        </div>
      </div>
    </section>

    <section class="container section split">
      <div class="card highlight-card">
        <div class="section-title">
          <h2>系统亮点</h2>
          <span>高并发保障与智能调度</span>
        </div>
        <div class="highlight-grid">
          <div v-for="highlight in highlights" :key="highlight.title" class="highlight-item">
            <h4>{{ highlight.title }}</h4>
            <p class="subtle">{{ highlight.desc }}</p>
          </div>
        </div>
      </div>

      <div class="card notice-card">
        <div class="section-title">
          <h2>通知中心</h2>
          <span>最近 24 小时</span>
        </div>
        <div class="notice-list">
          <div class="notice-row">
            <div>
              <p class="row-title">场地审批已通过</p>
              <p class="row-sub">社团联合活动 · 创新中心 3F</p>
            </div>
            <span class="notice-time">5 分钟前</span>
          </div>
          <div class="notice-row">
            <div>
              <p class="row-title">讲座报名提醒</p>
              <p class="row-sub">AI 伦理讲堂 · 还剩 12 个名额</p>
            </div>
            <span class="notice-time">2 小时前</span>
          </div>
          <div class="notice-row">
            <div>
              <p class="row-title">设备归还确认</p>
              <p class="row-sub">摄影器材 · 已完成消毒</p>
            </div>
            <span class="notice-time">昨天</span>
          </div>
        </div>
        <button class="btn ghost full">进入消息中心</button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page {
  padding: 36px 0 80px;
}

.hero {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 32px;
  align-items: center;
  margin-top: 16px;
}

.hero-copy h1 {
  font-family: 'Noto Serif SC', 'Songti SC', serif;
  font-size: 40px;
  line-height: 1.2;
  margin: 16px 0 12px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  margin-top: 28px;
}

.metric-value {
  font-size: 20px;
  font-weight: 600;
}

.metric-label {
  font-size: 13px;
  color: var(--text-muted);
}

.hero-panel {
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.panel-header h3 {
  font-size: 18px;
  margin-bottom: 4px;
}

.panel-list {
  display: grid;
  gap: 12px;
}

.panel-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #f8fafc;
  border-radius: var(--radius-md);
}

.row-title {
  font-weight: 600;
}

.row-sub {
  font-size: 13px;
  color: var(--text-muted);
}

.row-tag {
  padding: 4px 10px;
  background: rgba(29, 78, 216, 0.12);
  color: var(--primary);
  font-size: 12px;
  border-radius: 999px;
}

.panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  border-top: 1px dashed var(--border);
  padding-top: 14px;
}

.status-pill {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(14, 165, 233, 0.16);
  color: #0b72a6;
  font-size: 12px;
  font-weight: 600;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.service-card {
  padding: 18px;
  display: grid;
  gap: 12px;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.service-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.service-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: rgba(29, 78, 216, 0.12);
  color: var(--primary);
  display: grid;
  place-items: center;
}

.service-icon svg {
  width: 22px;
  height: 22px;
}

.service-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 6px;
}

.service-card h3 {
  font-size: 18px;
}

.service-tag {
  font-size: 12px;
  color: var(--text-muted);
  padding: 4px 10px;
  border-radius: 999px;
  background: #f1f5f9;
}

.split {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 20px;
}

.lecture-card,
.occupancy-card,
.highlight-card,
.notice-card {
  padding: 20px;
}

.lecture-list,
.notice-list {
  display: grid;
  gap: 16px;
}

.lecture-row,
.notice-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
}

.lecture-row:last-child,
.notice-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.lecture-tag {
  font-size: 12px;
  color: var(--primary);
  background: rgba(29, 78, 216, 0.12);
  padding: 4px 10px;
  border-radius: 999px;
}

.full {
  width: 100%;
  margin-top: 12px;
}

.occupancy-item {
  display: grid;
  gap: 8px;
  margin-bottom: 16px;
}

.occupancy-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--text-muted);
}

.bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 999px;
  overflow: hidden;
}

.bar span {
  display: block;
  height: 100%;
  background: linear-gradient(90deg, #1d4ed8, #0ea5e9);
  border-radius: inherit;
}

.highlight-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.highlight-item h4 {
  font-size: 16px;
  margin-bottom: 6px;
}

.notice-time {
  font-size: 12px;
  color: var(--text-light);
}

.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}

@media (max-width: 1024px) {
  .hero,
  .split {
    grid-template-columns: 1fr;
  }

  .hero-metrics {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-copy h1 {
    font-size: 30px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .highlight-grid {
    grid-template-columns: 1fr;
  }
}
</style>
