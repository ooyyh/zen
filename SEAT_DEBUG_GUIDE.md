## 座位可视化功能调试指南

### 问题：Failed to fetch / 服务器内部错误

### 检查清单

#### 1. 数据库表结构检查
```sql
-- 检查座位表是否有坐标字段
DESC seat;

-- 应该看到 position_x 和 position_y 字段
```

如果没有这些字段，运行：
```sql
-- 添加坐标字段
ALTER TABLE seat 
ADD COLUMN position_x INT COMMENT '座位X坐标',
ADD COLUMN position_y INT COMMENT '座位Y坐标';
```

#### 2. 后端编译检查
```bash
# 重新编译后端
mvn clean compile

# 检查是否有编译错误
# 特别注意 Seat.java, SeatView.java, SeatMapper.xml
```

#### 3. 启动后端并查看日志
```bash
mvn spring-boot:run

# 观察启动日志，看是否有错误
# 特别注意 MyBatis 映射文件是否加载成功
```

#### 4. 测试API端点

使用浏览器或Postman测试：

```
GET http://localhost:8080/api/admin/study-rooms
# 应返回自习室列表

GET http://localhost:8080/api/admin/study-rooms/1
# 应返回ID为1的自习室详情

GET http://localhost:8080/api/admin/study-rooms/1/seats
# 应返回自习室的座位列表
```

#### 5. 常见错误及解决方案

**错误1**: `Column 'position_x' not found`
- 原因：数据库表缺少字段
- 解决：运行 `db_init_seat_visualization.sql`

**错误2**: `Cannot resolve property 'positionX'`
- 原因：实体类字段映射问题
- 解决：检查 Seat.java 是否有 positionX 和 positionY 字段

**错误3**: `404 Not Found`
- 原因：Controller缺少对应的API
- 解决：检查 AdminStudyRoomController 是否有 `@GetMapping("/{id}")`

**错误4**: 批量保存失败
- 原因：INSERT语句中字段不匹配
- 解决：检查 SeatMapper.xml 的 insert 语句是否包含坐标字段

#### 6. 前端调试

打开浏览器开发者工具（F12）：

1. **Network标签页**：
   - 查看请求URL是否正确
   - 查看响应状态码和错误信息

2. **Console标签页**：
   - 查看是否有JavaScript错误

3. **检查请求数据**：
   ```javascript
   // 座位数据格式应该是：
   {
     "seatNo": "A01",
     "seatType": "单人",
     "hasPower": 0,
     "positionX": 50,
     "positionY": 50
   }
   ```

#### 7. 快速修复步骤

1. 停止后端服务
2. 运行SQL脚本添加字段：
   ```bash
   mysql -u root -p zen < src/main/resources/db_init_seat_visualization.sql
   ```
3. 重新编译：
   ```bash
   mvn clean compile
   ```
4. 启动后端：
   ```bash
   mvn spring-boot:run
   ```
5. 刷新前端页面

#### 8. 验证修复

访问：`http://localhost:3000/admin/study-rooms`

点击"座位管理"按钮：
- 应该能看到座位编辑器界面
- 左侧有模式选择器
- 中间有Canvas画布
- 下方有保存按钮

生成座位：
1. 选择"网格自动生成"
2. 输入行数：5，列数：10
3. 点击"生成座位"
4. 应该能在画布上看到座位
5. 点击"保存座位布局"
6. 应该显示"成功保存 50 个座位"

### 联系信息

如果以上步骤都无法解决问题，请提供：
1. 后端启动日志（最后100行）
2. 浏览器Console的错误信息
3. Network标签中失败请求的详细信息
