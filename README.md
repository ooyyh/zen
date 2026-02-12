# Zen Campus 校园综合服务系统 [狼堡六号]

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.13-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-3.0-4FC08D.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

一个全栈校园综合服务管理系统，提供教室预约、设备借用、讲座报名、校车预约、自习室抢座等功能。

[功能特性](#功能特性) • [技术栈](#技术栈) • [快速开始](#快速开始) • [项目结构](#项目结构) • [配置说明](#配置说明)

</div>

---

## 📋 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [API文档](#api文档)
- [用户角色](#用户角色)
- [开发指南](#开发指南)
- [部署指南](#部署指南)
- [常见问题](#常见问题)
- [更新日志](#更新日志)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

---

## ✨ 功能特性

### 核心功能

#### 🏫 教室预约系统
- 教室资源浏览与筛选
- 时间段预约管理
- 预约审批流程
- 我的预约记录查询

#### 📦 设备借用系统
- 设备目录管理
- 在线借用申请
- 审批流程（支持通过/驳回/归还）
- 设备状态实时追踪
- 冲突检测（同一设备同时段只能借用一次）

#### 🎓 讲座报名系统
- 讲座活动发布
- 在线报名管理
- 容量控制与候补机制
- 签到功能（支持二维码签到）
- 讲座统计分析

#### 🚌 校车预约系统
- 校车线路管理
- 班次排期管理
- 座位预约（先到先得）
- 候补名单管理
- 预约记录查询

#### 🪑 自习室抢座系统（⭐ 亮点功能）
- **Canvas可视化座位布局**
  - 支持拖拽式座位编辑
  - 自动网格生成
  - 座位位置精确到像素
- **实时座位状态同步**
  - WebSocket推送
  - 多用户实时可见
  - 显示预约中用户名
- **Redis分布式锁**
  - 防止超卖
  - 并发控制
  - 800ms锁等待时间
- **座位预约管理**
  - 时间段预约
  - 签到功能
  - 自动取消机制

#### 📢 通知系统
- 系统广播发布
- 消息模板管理
- 个人消息中心
- 多种通知类型（审批通知、活动通知等）

#### 📊 运营报表
- 实时数据统计
- 转化率分析
- 审批待办提醒
- 运营建议智能生成

---

## 🛠 技术栈

### 后端技术
- **框架**: Spring Boot 2.6.13
- **语言**: Java 1.8
- **ORM**: MyBatis 3.5.x
- **数据库**: MySQL 8.0
- **缓存/分布式锁**: Redis + Redisson
- **实时通信**: Spring WebSocket + STOMP
- **安全**: Spring Security + JWT
- **构建工具**: Maven 3.8+

### 前端技术
- **框架**: Vue 3.3 (Composition API)
- **构建工具**: Vite 4.3
- **路由**: Vue Router 4
- **状态管理**: Pinia (可选)
- **HTTP客户端**: Fetch API
- **WebSocket**: @stomp/stompjs
- **样式**: 原生CSS (支持暗黑模式)

### 开发工具
- **IDE**: IntelliJ IDEA / VS Code
- **API测试**: Postman
- **版本控制**: Git
- **数据库管理**: MySQL Workbench / Navicat

---

## 🏗 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                      用户层                               │
│  学生端 │ 教师端 │ 管理员端 │ 移动端(可扩展)              │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    前端展示层 (Vue 3)                      │
│  ├─ 路由守卫 (Vue Router)                                │
│  ├─ 状态管理 (响应式变量)                                 │
│  └─ UI组件 (自定义组件库)                                 │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                     API网关层                             │
│  ├─ JWT认证拦截器                                         │
│  ├─ 统一异常处理                                          │
│  └─ 跨域配置 (CORS)                                       │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  业务逻辑层 (Spring Boot)                  │
│  ├─ 预约服务 (ReservationService)                        │
│  ├─ 设备服务 (EquipmentService)                          │
│  ├─ 讲座服务 (LectureService)                            │
│  ├─ 校车服务 (BusService)                                │
│  ├─ 座位服务 (SeatReservationService)                    │
│  └─ 通知服务 (MessageService)                            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  数据访问层 (MyBatis)                      │
│  ├─ XML映射文件                                           │
│  ├─ 实体类 (Entity)                                      │
│  └─ 数据传输对象 (DTO)                                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌──────────────────┬──────────────────┬──────────────────┐
│   MySQL 数据库    │   Redis 缓存      │  WebSocket 服务  │
│  持久化存储       │  分布式锁/缓存    │  实时通信         │
└──────────────────┴──────────────────┴──────────────────┘
```

---

## 🚀 快速开始

### 环境要求

- **JDK**: 1.8+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+ (可选，用于分布式锁)
- **Node.js**: 16.0+
- **npm**: 8.0+

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/zen-campus.git
cd zen-campus
```

#### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE zen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE zen;

# 导入初始化脚本
source src/main/resources/schema.sql;
source src/main/resources/data.sql;

# 如果需要座位可视化功能，执行以下迁移
source src/main/resources/db_migration_add_study_room_seat_reservation.sql;
source src/main/resources/db_migration_add_seat_position.sql;
source src/main/resources/db_migration_add_user_real_name.sql;
source src/main/resources/db_init_seat_visualization.sql;
```

#### 3. 配置后端

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zen?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: your_password
    
  # Redis配置（可选）
  redis:
    host: 59.110.93.61
    port: 6379
    password: # 如果有密码请填写

# JWT密钥（生产环境请修改）
app:
  jwt:
    secret: your-secret-key-change-in-production
```

#### 4. 启动后端

```bash
# 方式1: Maven命令
mvn clean package -DskipTests
mvn spring-boot:run

# 方式2: IDEA直接运行
# 运行 ZenApplication.java 的 main 方法

# 方式3: 使用提供的脚本
.\start-backend.bat

# 后端将运行在 http://localhost:8080
```

#### 5. 安装前端依赖

```bash
cd zen-ui
npm install

# 如果需要WebSocket功能
npm install @stomp/stompjs
```

#### 6. 启动前端

```bash
# 开发模式
npm run dev

# 前端将运行在 http://localhost:5173
```

#### 7. 访问系统

打开浏览器访问: `http://localhost:5173`

**默认管理员账号**:
- 用户名: `admin`
- 密码: `admin123`

---

## 📁 项目结构

```
zen-campus/
├── src/main/java/com/hbnu/zen/        # 后端源码
│   ├── common/                         # 公共类
│   │   ├── ApiResponse.java           # 统一响应格式
│   │   ├── AuthUtil.java              # 认证工具类
│   │   ├── BusinessException.java     # 业务异常
│   │   ├── ErrorCode.java             # 错误码枚举
│   │   └── Role.java                  # 角色常量
│   ├── config/                         # 配置类
│   │   ├── SecurityConfig.java        # 安全配置
│   │   ├── RedissonConfig.java        # Redisson配置
│   │   └── WebSocketConfig.java       # WebSocket配置
│   ├── controller/                     # 控制器
│   │   ├── AuthController.java        # 认证接口
│   │   ├── ReservationController.java # 预约接口
│   │   ├── EquipmentController.java   # 设备接口
│   │   ├── LectureController.java     # 讲座接口
│   │   ├── BusController.java         # 校车接口
│   │   ├── StudyRoomController.java   # 自习室接口
│   │   └── ...                        # 其他控制器
│   ├── dto/                            # 数据传输对象
│   ├── mapper/                         # MyBatis Mapper接口
│   ├── mybatis/entity/                 # 实体类
│   ├── security/                       # 安全相关
│   │   ├── JwtUtil.java               # JWT工具类
│   │   └── JwtAuthFilter.java         # JWT过滤器
│   └── service/                        # 业务逻辑层
│       ├── ReservationService.java
│       ├── EquipmentBorrowService.java
│       ├── SeatReservationService.java # 座位预约(含分布式锁)
│       └── ...
├── src/main/resources/
│   ├── application.yml                 # 应用配置
│   ├── schema.sql                      # 数据库表结构
│   ├── data.sql                        # 初始数据
│   ├── db_migration_*.sql              # 数据库迁移脚本
│   └── mappers/                        # MyBatis XML映射文件
├── zen-ui/                             # 前端项目
│   ├── src/
│   │   ├── components/                 # Vue组件
│   │   │   ├── AppShell.vue           # 主框架
│   │   │   ├── ConfirmDialog.vue      # 确认对话框
│   │   │   ├── SeatLayoutEditor.vue   # 座位布局编辑器
│   │   │   └── ...
│   │   ├── views/                      # 页面视图
│   │   │   ├── LoginView.vue          # 登录页
│   │   │   ├── RegisterView.vue       # 注册页
│   │   │   ├── DashboardView.vue      # 工作台
│   │   │   ├── SeatSelectionView.vue  # 座位选择(Canvas+WebSocket)
│   │   │   ├── AdminReportView.vue    # 运营报表
│   │   │   └── ...
│   │   ├── services/                   # 服务层
│   │   │   ├── api.js                 # API封装
│   │   │   ├── auth.js                # 认证服务
│   │   │   └── websocket.js           # WebSocket服务
│   │   ├── router/                     # 路由配置
│   │   ├── assets/                     # 静态资源
│   │   └── App.vue                     # 根组件
│   ├── package.json
│   └── vite.config.js                  # Vite配置
├── pom.xml                             # Maven配置
├── .editorconfig                       # 编辑器配置(UTF-8)
├── CLAUDE.md                           # Claude AI工作指南
└── README.md                           # 本文件
```

---

## ⚙️ 配置说明

### 后端配置文件 (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: zen
  
  datasource:
    url: jdbc:mysql://localhost:3306/zen
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: 59.110.93.61
    port: 6379
    password: 
  
mybatis:
  mapper-locations: classpath:mappers/*.xml
  type-aliases-package: com.hbnu.zen.mybatis.entity

app:
  jwt:
    secret: zen-secret-key-2024-change-in-production
    expiration: 604800000  # 7天（毫秒）
  
  seat:
    lock-wait-ms: 800      # 座位锁等待时间
    lock-lease-ms: 5000    # 座位锁租期
```

### 前端配置文件 (vite.config.js)

```javascript
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  define: {
    global: 'globalThis'  // WebSocket polyfill
  }
})
```

---

## 📚 API文档

### 认证接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录 | 公开 |
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/me` | 获取当前用户信息 | 需登录 |

### 教室预约接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/classrooms` | 获取教室列表 | 需登录 |
| POST | `/api/reservations` | 创建预约 | 需登录 |
| GET | `/api/reservations/my` | 我的预约列表 | 需登录 |
| DELETE | `/api/reservations/{id}` | 取消预约 | 需登录 |

### 设备借用接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/equipments` | 获取设备列表 | 需登录 |
| POST | `/api/equipments/borrow` | 借用设备 | 需登录 |
| GET | `/api/equipments/borrows/my` | 我的借用记录 | 需登录 |
| POST | `/api/equipments/borrows/{id}/return` | 归还设备 | 需登录 |

### 自习室座位接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/study-rooms` | 获取自习室列表 | 需登录 |
| GET | `/api/study-rooms/{id}/seats` | 获取可用座位 | 需登录 |
| POST | `/api/seat-reservations` | 预约座位 | 需登录 |
| GET | `/api/seat-status/pending/{roomId}` | 获取pending座位状态 | 需登录 |
| POST | `/api/seat-status/pending/{seatId}` | 标记座位为pending | 需登录 |

### 管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/admin/reports/overview` | 运营报表 | 管理员 |
| GET | `/api/admin/equipments/borrows` | 借用审批列表 | 管理员/教师 |
| POST | `/api/admin/equipments/borrows/{id}/approve` | 审批通过 | 管理员/教师 |
| POST | `/api/admin/equipments/borrows/{id}/reject` | 审批驳回 | 管理员/教师 |

### WebSocket接口

| 端点 | 说明 |
|------|------|
| `/ws` | WebSocket连接端点 |
| `/topic/seat-status` | 订阅座位状态更新 |

---

## 👥 用户角色

### 学生 (STUDENT)
- ✅ 预约教室
- ✅ 借用设备
- ✅ 报名讲座
- ✅ 预约校车
- ✅ 抢座自习室
- ✅ 查看个人记录

### 教师 (TEACHER)
- ✅ 学生所有权限
- ✅ 管理讲座活动
- ✅ 审批设备借用
- ✅ 管理自习室
- ✅ 发布通知

### 管理员 (ADMIN)
- ✅ 教师所有权限
- ✅ 用户管理
- ✅ 系统配置
- ✅ 运营报表
- ✅ 消息模板管理
- ✅ 全局设置

---

## 🔧 开发指南

### 后端开发

#### 添加新实体

1. 创建Entity类 (`src/main/java/com/hbnu/zen/mybatis/entity/`)
2. 创建Mapper接口 (`src/main/java/com/hbnu/zen/mapper/`)
3. 创建XML映射文件 (`src/main/resources/mappers/`)
4. 创建Service (`src/main/java/com/hbnu/zen/service/`)
5. 创建Controller (`src/main/java/com/hbnu/zen/controller/`)

#### 使用Redis分布式锁

```java
@Autowired
private RedissonClient redissonClient;

public void doSomething(Long resourceId) {
    String lockKey = "lock:resource:" + resourceId;
    RLock lock = redissonClient.getLock(lockKey);
    
    try {
        if (lock.tryLock(800, 5000, TimeUnit.MILLISECONDS)) {
            try {
                // 业务逻辑
            } finally {
                lock.unlock();
            }
        } else {
            throw new BusinessException(ErrorCode.CONFLICT, "Resource is busy");
        }
    } catch (InterruptedException e) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Lock interrupted");
    }
}
```

#### 使用WebSocket广播

```java
@Autowired
private SimpMessagingTemplate messagingTemplate;

public void broadcastUpdate(Object message) {
    messagingTemplate.convertAndSend("/topic/updates", message);
}
```

### 前端开发

#### 添加新页面

1. 创建Vue组件 (`zen-ui/src/views/`)
2. 在路由中注册 (`zen-ui/src/router/index.js`)
3. 在导航菜单中添加入口 (`AppShell.vue`)

#### 调用API

```javascript
import { request } from '@/services/api'

// GET请求
const data = await request('/api/classrooms')

// POST请求
const result = await request('/api/reservations', {
  method: 'POST',
  body: JSON.stringify({ roomId: 1, startTime: '...' })
})
```

#### 使用WebSocket

```javascript
import websocket from '@/services/websocket'

// 连接
websocket.connect(() => {
  // 订阅主题
  websocket.subscribe('/topic/seat-status', (message) => {
    console.log('Received:', message)
  })
})

// 断开
onUnmounted(() => {
  websocket.disconnect()
})
```

---

## 🚢 部署指南

### 后端部署

#### 打包

```bash
mvn clean package -DskipTests
```

生成的JAR文件位于 `target/zen-0.0.1-SNAPSHOT.jar`

#### 运行

```bash
# 生产环境运行
java -jar target/zen-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 自定义端口
java -jar target/zen-0.0.1-SNAPSHOT.jar --server.port=9090

# 后台运行
nohup java -jar target/zen-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### 前端部署

#### 构建

```bash
cd zen-ui
npm run build
```

生成的静态文件位于 `zen-ui/dist/`

#### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/zen-ui/dist;
    index index.html;
    
    # Vue Router history模式
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 代理后端API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # WebSocket代理
    location /ws {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## ❓ 常见问题

### 1. 编译时出现中文乱码

**问题**: Java文件中的中文字符串显示为 `???`

**解决**:
```bash
# 使用UTF-8编码重新编译
mvn clean compile -Dproject.build.sourceEncoding=UTF-8

# 或使用提供的脚本
.\rebuild.bat
```

### 2. WebSocket连接失败

**问题**: 前端无法连接WebSocket

**解决**:
- 检查后端WebSocket配置是否正确
- 确认端口8080未被占用
- 检查浏览器控制台是否有CORS错误
- 确认已安装 `@stomp/stompjs` 依赖

### 3. Redis连接失败

**问题**: 座位预约功能无法使用

**解决**:
- 确认Redis服务已启动
- 检查 `application.yml` 中的Redis配置
- 如果没有Redis，可以注释掉Redisson相关配置（但会失去分布式锁功能）

### 4. 数据库连接失败

**问题**: 启动时报数据库连接错误

**解决**:
- 确认MySQL服务已启动
- 检查数据库名、用户名、密码是否正确
- 确认数据库已创建: `CREATE DATABASE zen`
- 检查时区设置: `serverTimezone=Asia/Shanghai`

### 5. 前端页面空白

**问题**: 打开页面后显示空白

**解决**:
- 检查浏览器控制台是否有JavaScript错误
- 确认后端API是否正常运行
- 清除浏览器缓存
- 检查Vue路由配置

---

## 📝 更新日志

### v1.0.0 (2026-02-09)

#### 新增功能
- ✨ 完整的用户认证系统（登录/注册/JWT）
- ✨ 三种用户角色（学生/教师/管理员）
- ✨ 教室预约系统
- ✨ 设备借用系统（含审批流程）
- ✨ 讲座报名系统（含签到功能）
- ✨ 校车预约系统（含候补机制）
- ✨ 自习室抢座系统
  - Canvas可视化座位布局
  - 拖拽式座位编辑器
  - WebSocket实时状态同步
  - Redis分布式锁防超卖
- ✨ 通知系统（广播/消息模板）
- ✨ 运营报表（实时统计分析）

#### 技术亮点
- 🚀 分布式锁保证并发安全
- 🎨 Canvas实现座位可视化
- ⚡ WebSocket实时通信
- 📱 响应式设计（支持移动端）
- 🌙 暗黑模式支持
- 🔐 JWT无状态认证

#### Bug修复
- 🐛 修复设备归还后按钮显示问题
- 🐛 修复中文字符编码问题
- 🐛 修复时区转换导致的预约时间验证错误
- 🐛 修复报表统计不包含已归还设备的问题
- 🐛 修复座位重复编号检测

---

## 🤝 贡献指南

欢迎贡献代码、报告Bug或提出新功能建议！

### 贡献流程

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

### 代码规范

- 后端: 遵循阿里巴巴Java开发手册
- 前端: 遵循Vue官方风格指南
- 提交信息: 使用语义化提交信息（Conventional Commits）

### 开发建议

- 所有Java源文件使用UTF-8编码
- 前端组件使用Composition API
- API接口统一使用RESTful风格
- 数据库字段使用snake_case，Java使用camelCase
- 敏感信息不要提交到代码库

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 📧 联系方式

- 项目地址: [https://github.com/yourusername/zen-campus](https://github.com/yourusername/zen-campus)
- Issue反馈: [https://github.com/yourusername/zen-campus/issues](https://github.com/yourusername/zen-campus/issues)
- 邮箱: your.email@example.com

---

## 🙏 致谢

感谢所有为本项目做出贡献的开发者！

特别感谢：
- Spring Boot团队提供的优秀框架
- Vue.js团队提供的前端框架
- Redis和Redisson提供的分布式解决方案
- MyBatis团队提供的持久层框架

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给它一个Star！⭐**

Made with ❤️ by Zen Campus Team

</div>
