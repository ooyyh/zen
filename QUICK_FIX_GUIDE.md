## 座位可视化功能 - 快速修复步骤

### 问题分析
1. **Failed to fetch** - 可能原因：数据库缺少 position_x/position_y 字段
2. **服务器内部错误** - 可能原因：MyBatis映射失败或字段不匹配

### 🔧 必须执行的步骤

#### 步骤1：添加数据库字段（最关键！）

打开MySQL客户端或数据库管理工具，执行：

```sql
-- 连接到zen数据库
USE zen;

-- 检查seat表结构
DESC seat;

-- 如果没有position_x和position_y字段，执行下面的语句：
ALTER TABLE seat 
ADD COLUMN position_x INT COMMENT '座位X坐标',
ADD COLUMN position_y INT COMMENT '座位Y坐标';

-- 再次检查确认字段已添加
DESC seat;
```

#### 步骤2：重新编译后端

```bash
# 在项目根目录执行
mvn clean compile

# 或使用提供的脚本
.\build.bat
```

#### 步骤3：重启后端服务

```bash
# 停止当前运行的后端（如果有）
# 然后启动
mvn spring-boot:run
```

观察启动日志，确保没有错误。

#### 步骤4：刷新前端

```bash
# 如果前端正在运行，只需刷新浏览器
# 如果没有运行，启动前端：
cd zen-ui
npm run dev
```

### 🧪 测试步骤

1. **访问自习室管理**：
   - URL: `http://localhost:5173/admin/study-rooms` （或你的前端端口）
   - 应该能看到自习室列表

2. **点击"座位管理"**：
   - 应该能进入座位编辑页面
   - 看到模式选择器和Canvas画布

3. **测试网格生成**：
   - 选择"网格自动生成"
   - 输入：行数=3，列数=5，前缀=A
   - 点击"生成座位"
   - 应该在Canvas上看到15个座位

4. **测试保存**：
   - 点击"保存座位布局"
   - 应该显示"成功保存 15 个座位！"
   - 自动跳转回自习室列表

### 📋 验证清单

- [ ] 数据库seat表有position_x和position_y字段
- [ ] 后端编译成功无错误
- [ ] 后端启动成功无错误  
- [ ] 能访问 http://localhost:8080/api/admin/study-rooms
- [ ] 能访问 http://localhost:8080/api/admin/study-rooms/1
- [ ] 能访问 http://localhost:8080/api/admin/study-rooms/1/seats
- [ ] 前端能正常加载座位管理页面
- [ ] 能生成座位并在Canvas上显示
- [ ] 能成功保存座位布局

### 🐛 如果还是失败

请打开浏览器开发者工具（F12），查看：

1. **Console标签页**的错误信息
2. **Network标签页**中失败的请求：
   - 点击失败的请求
   - 查看"Response"标签页
   - 复制完整的错误信息

然后告诉我具体的错误内容，我会继续帮你解决！
