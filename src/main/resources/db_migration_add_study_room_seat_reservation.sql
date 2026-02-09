-- 添加自习室抢座功能相关表
-- 创建时间: 2026-02-09
-- 说明: 包含自习室、座位、座位预约三张表

-- 自习室表
CREATE TABLE IF NOT EXISTS study_room (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL COMMENT '自习室名称',
  building VARCHAR(64) NOT NULL COMMENT '所在楼栋',
  floor INT NOT NULL COMMENT '楼层',
  area VARCHAR(64) COMMENT '区域（如东区、西区）',
  total_seats INT NOT NULL COMMENT '座位总数',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室表';

-- 座位表
CREATE TABLE IF NOT EXISTS seat (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  study_room_id BIGINT NOT NULL COMMENT '所属自习室ID',
  seat_no VARCHAR(32) NOT NULL COMMENT '座位编号',
  seat_type VARCHAR(32) COMMENT '座位类型（如单人、双人）',
  has_power TINYINT NOT NULL DEFAULT 0 COMMENT '是否有电源：1-有 0-无',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_seat (study_room_id, seat_no),
  KEY idx_seat_room (study_room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- 座位预约表
CREATE TABLE IF NOT EXISTS seat_reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seat_id BIGINT NOT NULL COMMENT '座位ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  start_time DATETIME NOT NULL COMMENT '预约开始时间',
  end_time DATETIME NOT NULL COMMENT '预约结束时间',
  status VARCHAR(20) NOT NULL COMMENT '状态：RESERVED-已预约 CHECKED_IN-已签到 COMPLETED-已完成 CANCELED-已取消 EXPIRED-已过期',
  check_in_at DATETIME COMMENT '签到时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_seat_reservation_seat (seat_id),
  KEY idx_seat_reservation_user (user_id),
  KEY idx_seat_reservation_time (start_time, end_time),
  KEY idx_seat_reservation_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位预约表';

-- 插入测试数据（可选）
INSERT INTO study_room (name, building, floor, area, total_seats) VALUES
('第一自习室', '图书馆', 3, '东区', 80),
('第二自习室', '图书馆', 4, '西区', 100),
('第三自习室', '教学楼A', 2, NULL, 60);

-- 为第一自习室创建座位（示例）
INSERT INTO seat (study_room_id, seat_no, seat_type, has_power) VALUES
(1, 'A01', '单人', 1),
(1, 'A02', '单人', 1),
(1, 'A03', '单人', 0),
(1, 'A04', '单人', 0),
(1, 'B01', '单人', 1),
(1, 'B02', '单人', 1),
(1, 'B03', '单人', 0),
(1, 'B04', '单人', 0);
