/*
 Navicat Premium Data Transfer

 Source Server         : LocalMysql
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : localhost:3306
 Source Schema         : zen

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 23/02/2026 10:59:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for approval_task
-- ----------------------------
DROP TABLE IF EXISTS `approval_task`;
CREATE TABLE `approval_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `approver_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `approver_id` bigint NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_approval_reservation`(`reservation_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`status` ASC) USING BTREE
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of approval_task
-- ----------------------------

-- ----------------------------
-- Table structure for bus_booking
-- ----------------------------
DROP TABLE IF EXISTS `bus_booking`;
CREATE TABLE `bus_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `trip_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bus_booking_trip`(`trip_id` ASC) USING BTREE,
  INDEX `idx_bus_booking_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_bus_booking_status`(`status` ASC) USING BTREE
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bus_booking
-- ----------------------------

-- ----------------------------
-- Table structure for bus_route
-- ----------------------------
DROP TABLE IF EXISTS `bus_route`;
CREATE TABLE `bus_route`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `origin` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `destination` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bus_route
-- ----------------------------
INSERT INTO `bus_route` VALUES (1, '问山居-东大门', '问山居', '集贤阁', 1, '2026-02-09 20:06:34', '2026-02-09 20:06:34');

-- ----------------------------
-- Table structure for bus_trip
-- ----------------------------
DROP TABLE IF EXISTS `bus_trip`;
CREATE TABLE `bus_trip`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `route_id` bigint NOT NULL,
  `bus_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL,
  `capacity` int NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bus_trip_route`(`route_id` ASC) USING BTREE,
  INDEX `idx_bus_trip_time`(`departure_time` ASC) USING BTREE
) AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bus_trip
-- ----------------------------
INSERT INTO `bus_trip` VALUES (1, 1, '鄂G1432', '2026-02-09 20:00:00', '2026-02-09 20:30:00', 40, 'OPEN', '2026-02-09 20:07:25', '2026-02-09 20:07:25');

-- ----------------------------
-- Table structure for classroom
-- ----------------------------
DROP TABLE IF EXISTS `classroom`;
CREATE TABLE `classroom`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `building` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `room_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `capacity` int NOT NULL,
  `location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `equipment_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_classroom`(`building` ASC, `room_no` ASC) USING BTREE
) AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom
-- ----------------------------
INSERT INTO `classroom` VALUES (1, '幸运楼', '101', 1000, '学习大楼A', '实验室', 1, '2026-02-09 17:14:16', '2026-02-09 17:14:16');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `asset_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `total_qty` int NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_equipment_asset`(`asset_no` ASC) USING BTREE
) AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '单反相机', '摄影设备', 'CC001', '快乐楼1栋', 1, 1, '2026-02-09 16:15:07', '2026-02-09 16:15:07');

-- ----------------------------
-- Table structure for equipment_borrow
-- ----------------------------
DROP TABLE IF EXISTS `equipment_borrow`;
CREATE TABLE `equipment_borrow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `equipment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `approved_by` bigint NULL DEFAULT NULL,
  `approved_at` datetime NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `returned_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment_borrow_equipment`(`equipment_id` ASC) USING BTREE,
  INDEX `idx_equipment_borrow_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_equipment_borrow_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_equipment_borrow_status`(`status` ASC) USING BTREE
) AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment_borrow
-- ----------------------------
INSERT INTO `equipment_borrow` VALUES (1, 1, 1, '2026-02-10 17:15:00', '2026-02-10 20:15:00', 'REJECTED', '拍照', 1, '2026-02-09 16:16:44', '坏了在维修', NULL, '2026-02-09 16:16:01', '2026-02-09 16:16:44');
INSERT INTO `equipment_borrow` VALUES (2, 1, 1, '2026-02-09 17:12:00', '2026-02-09 20:12:00', 'RETURNED', '拍照', 1, '2026-02-09 17:13:07', '', '2026-02-09 17:13:17', '2026-02-09 17:12:25', '2026-02-09 17:13:17');

-- ----------------------------
-- Table structure for lecture
-- ----------------------------
DROP TABLE IF EXISTS `lecture`;
CREATE TABLE `lecture`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `speaker` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `capacity` int NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_lecture_time`(`start_time` ASC, `end_time` ASC) USING BTREE
) AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lecture
-- ----------------------------
INSERT INTO `lecture` VALUES (1, 'vibe coding课程讲解', 'B2', '林云楼1202', '2026-02-10 19:00:00', '2026-02-10 20:30:00', 50, 'OPEN', '2026-02-09 19:37:49', '2026-02-09 19:37:49');

-- ----------------------------
-- Table structure for lecture_checkin
-- ----------------------------
DROP TABLE IF EXISTS `lecture_checkin`;
CREATE TABLE `lecture_checkin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lecture_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `check_in_at` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_lecture_checkin`(`lecture_id` ASC, `user_id` ASC) USING BTREE
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lecture_checkin
-- ----------------------------

-- ----------------------------
-- Table structure for lecture_signup
-- ----------------------------
DROP TABLE IF EXISTS `lecture_signup`;
CREATE TABLE `lecture_signup`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lecture_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_lecture_user`(`lecture_id` ASC, `user_id` ASC) USING BTREE
) AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lecture_signup
-- ----------------------------
INSERT INTO `lecture_signup` VALUES (1, 1, 1, 'SIGNED_UP', '2026-02-09 19:38:09');
INSERT INTO `lecture_signup` VALUES (2, 1, 3, 'SIGNED_UP', '2026-02-09 19:40:57');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `template_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_message_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_message_status`(`status` ASC) USING BTREE
) AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 1, 'EQUIPMENT_BORROW_CREATED', '设备借用已提交', '你的设备借用申请已提交，等待审批。设备：单反相机(CC001) 2026-02-10 17:15-2026-02-10 20:15', 'READ', '2026-02-09 16:16:01', '2026-02-09 17:12:50');
INSERT INTO `message` VALUES (2, 1, 'EQUIPMENT_BORROW_REJECTED', '设备借用被驳回', '你的设备借用被驳回。设备：单反相机(CC001) 原因：坏了在维修', 'READ', '2026-02-09 16:16:44', '2026-02-09 17:12:50');
INSERT INTO `message` VALUES (3, 1, 'EQUIPMENT_BORROW_CREATED', '设备借用已提交', '你的设备借用申请已提交，等待审批。设备：单反相机(CC001) 2026-02-09 17:12-2026-02-09 20:12', 'READ', '2026-02-09 17:12:25', '2026-02-09 17:12:48');
INSERT INTO `message` VALUES (4, 1, 'EQUIPMENT_BORROW_APPROVED', '设备借用已通过', '你的设备借用已通过审批。设备：单反相机(CC001) 2026-02-09 17:12-2026-02-09 20:12', 'UNREAD', '2026-02-09 17:13:07', NULL);
INSERT INTO `message` VALUES (5, 1, 'EQUIPMENT_BORROW_RETURNED', '设备归还确认', '设备归还已确认。设备：单反相机(CC001)', 'UNREAD', '2026-02-09 17:13:17', NULL);
INSERT INTO `message` VALUES (6, 1, 'LECTURE_SIGNUP_SUCCESS', '讲座报名成功', '你已报名讲座：vibe coding课程讲解，时间：2026-02-10 19:00', 'UNREAD', '2026-02-09 19:38:09', NULL);
INSERT INTO `message` VALUES (7, 3, 'LECTURE_SIGNUP_SUCCESS', '讲座报名成功', '你已报名讲座：vibe coding课程讲解，时间：2026-02-10 19:00', 'READ', '2026-02-09 19:40:57', '2026-02-09 19:41:20');
INSERT INTO `message` VALUES (8, 3, 'BROADCAST', '系统更新', '本次更新releaseV1.3.5\n- 新增自习室相关功能\n- 改良了用户体验\n- 增加了更符合直觉的UI\n2025-11-20\nBy ooyyh', 'UNREAD', '2026-02-09 19:55:09', NULL);
INSERT INTO `message` VALUES (9, 2, 'BROADCAST', '系统更新', '本次更新releaseV1.3.5\n- 新增自习室相关功能\n- 改良了用户体验\n- 增加了更符合直觉的UI\n2025-11-20\nBy ooyyh', 'UNREAD', '2026-02-09 19:55:09', NULL);
INSERT INTO `message` VALUES (10, 1, 'BROADCAST', '系统更新', '本次更新releaseV1.3.5\n- 新增自习室相关功能\n- 改良了用户体验\n- 增加了更符合直觉的UI\n2025-11-20\nBy ooyyh', 'UNREAD', '2026-02-09 19:55:09', NULL);

-- ----------------------------
-- Table structure for message_template
-- ----------------------------
DROP TABLE IF EXISTS `message_template`;
CREATE TABLE `message_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_code`(`template_code` ASC) USING BTREE
) AUTO_INCREMENT = 415 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_template
-- ----------------------------
INSERT INTO `message_template` VALUES (1, 'RESERVATION_CREATED', '预约提交成功', '你的预约申请已提交，等待审批。预约：{classroom} {startTime}-{endTime}', '2026-02-09 01:22:48', '2026-02-09 01:22:48');
INSERT INTO `message_template` VALUES (2, 'RESERVATION_APPROVED', '预约已通过', '你的预约已通过审批。预约：{classroom} {startTime}-{endTime}', '2026-02-09 01:22:48', '2026-02-09 01:22:48');
INSERT INTO `message_template` VALUES (3, 'RESERVATION_REJECTED', '预约被驳回', '你的预约被驳回。原因：{remark}', '2026-02-09 01:22:48', '2026-02-09 01:22:48');
INSERT INTO `message_template` VALUES (4, 'LECTURE_SIGNUP_SUCCESS', '讲座报名成功', '你已报名讲座：{lectureTitle}，时间：{startTime}', '2026-02-09 01:22:48', '2026-02-09 01:22:48');
INSERT INTO `message_template` VALUES (5, 'LECTURE_WAITLIST', '讲座候补提醒', '你已进入讲座候补队列：{lectureTitle}', '2026-02-09 01:22:48', '2026-02-09 01:22:48');
INSERT INTO `message_template` VALUES (11, 'EQUIPMENT_BORROW_CREATED', '设备借用已提交', '你的设备借用申请已提交，等待审批。设备：{equipment} {startTime}-{endTime}', '2026-02-09 02:07:35', '2026-02-09 02:07:35');
INSERT INTO `message_template` VALUES (12, 'EQUIPMENT_BORROW_APPROVED', '设备借用已通过', '你的设备借用已通过审批。设备：{equipment} {startTime}-{endTime}', '2026-02-09 02:07:35', '2026-02-09 02:07:35');
INSERT INTO `message_template` VALUES (13, 'EQUIPMENT_BORROW_REJECTED', '设备借用被驳回', '你的设备借用被驳回。设备：{equipment} 原因：{remark}', '2026-02-09 02:07:35', '2026-02-09 02:07:35');
INSERT INTO `message_template` VALUES (14, 'EQUIPMENT_BORROW_RETURNED', '设备归还确认', '设备归还已确认。设备：{equipment}', '2026-02-09 02:07:35', '2026-02-09 02:07:35');
INSERT INTO `message_template` VALUES (33, 'BUS_BOOKED', '校车预约成功', '你已成功预约校车：{route} 发车时间 {departureTime}', '2026-02-09 11:04:39', '2026-02-09 11:04:39');
INSERT INTO `message_template` VALUES (34, 'BUS_WAITLIST', '校车候补排队中', '你已进入校车候补队列：{route} 发车时间 {departureTime}', '2026-02-09 11:04:39', '2026-02-09 11:04:39');
INSERT INTO `message_template` VALUES (35, 'BUS_CANCELED', '校车预约已取消', '你的校车预约已取消：{route} 发车时间 {departureTime}', '2026-02-09 11:04:39', '2026-02-09 11:04:39');
INSERT INTO `message_template` VALUES (36, 'BUS_PROMOTED', '校车候补转正', '候补成功转正：{route} 发车时间 {departureTime}', '2026-02-09 11:04:39', '2026-02-09 11:04:39');
INSERT INTO `message_template` VALUES (37, 'LECTURE_CHECKIN_SUCCESS', '讲座签到成功', '你已完成讲座签到：{lectureTitle} 时间 {startTime}', '2026-02-09 11:04:39', '2026-02-09 11:04:39');

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `classroom_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `approval_required` tinyint NOT NULL DEFAULT 1,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reservation_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_reservation_room`(`classroom_id` ASC) USING BTREE,
  INDEX `idx_reservation_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_reservation_status`(`status` ASC) USING BTREE
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation
-- ----------------------------

-- ----------------------------
-- Table structure for seat
-- ----------------------------
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `study_room_id` bigint NOT NULL,
  `seat_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `seat_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `position_x` int NULL DEFAULT NULL COMMENT '座位X坐标（Canvas坐标）',
  `position_y` int NULL DEFAULT NULL COMMENT '座位Y坐标（Canvas坐标）',
  `has_power` tinyint NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_seat`(`study_room_id` ASC, `seat_no` ASC) USING BTREE,
  INDEX `idx_seat_room`(`study_room_id` ASC) USING BTREE
) AUTO_INCREMENT = 289 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seat
-- ----------------------------
INSERT INTO `seat` VALUES (260, 1, 'A01-01', '单人', 50, 50, 0, 1, '2026-02-09 17:45:35', '2026-02-09 17:45:35');
INSERT INTO `seat` VALUES (261, 1, 'A01-02', '单人', 100, 50, 0, 1, '2026-02-09 17:45:35', '2026-02-09 17:45:35');
INSERT INTO `seat` VALUES (262, 1, 'A01-03', '单人', 150, 50, 0, 1, '2026-02-09 17:45:35', '2026-02-09 17:45:35');
INSERT INTO `seat` VALUES (263, 1, 'A01-04', '单人', 200, 50, 0, 1, '2026-02-09 17:45:35', '2026-02-09 17:45:35');
INSERT INTO `seat` VALUES (264, 1, 'A01-05', '单人', 250, 50, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (265, 1, 'A02-01', '单人', 50, 100, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (266, 1, 'A02-02', '单人', 100, 100, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (267, 1, 'A02-03', '单人', 150, 100, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (268, 1, 'A02-04', '单人', 200, 100, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (269, 1, 'A02-05', '单人', 250, 100, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (270, 1, 'A03-01', '单人', 50, 150, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (271, 1, 'A03-02', '单人', 100, 150, 0, 1, '2026-02-09 17:45:36', '2026-02-09 17:45:36');
INSERT INTO `seat` VALUES (272, 1, 'A03-03', '单人', 150, 150, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (273, 1, 'A03-04', '单人', 200, 150, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (274, 1, 'A03-05', '单人', 250, 150, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (275, 1, 'A04-01', '单人', 50, 200, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (276, 1, 'A04-02', '单人', 100, 200, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (277, 1, 'A04-03', '单人', 150, 200, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (278, 1, 'A04-04', '单人', 200, 200, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (279, 1, 'A04-05', '单人', 250, 200, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (280, 1, 'A05-01', '单人', 50, 250, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (281, 1, 'A05-02', '单人', 100, 250, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (282, 1, 'A05-03', '单人', 150, 250, 0, 1, '2026-02-09 17:45:37', '2026-02-09 17:45:37');
INSERT INTO `seat` VALUES (283, 1, 'A05-04', '单人', 200, 250, 0, 1, '2026-02-09 17:45:38', '2026-02-09 17:45:38');
INSERT INTO `seat` VALUES (284, 1, 'A05-05', '单人', 250, 250, 0, 1, '2026-02-09 17:45:38', '2026-02-09 17:45:38');
INSERT INTO `seat` VALUES (285, 2, '第二自习室01-01', '单人', 230, 121, 0, 1, '2026-02-09 17:51:36', '2026-02-09 17:51:36');
INSERT INTO `seat` VALUES (286, 2, '第二自习室02-01', '单人', 50, 100, 0, 1, '2026-02-09 17:51:37', '2026-02-09 17:51:37');
INSERT INTO `seat` VALUES (287, 2, '第二自习室03-01', '单人', 299, 224, 0, 1, '2026-02-09 17:51:37', '2026-02-09 17:51:37');
INSERT INTO `seat` VALUES (288, 2, '第二自习室04-01', '单人', 50, 200, 0, 1, '2026-02-09 17:51:37', '2026-02-09 17:51:37');
INSERT INTO `seat` VALUES (289, 2, '第二自习室05-01', '单人', 470, 137, 0, 1, '2026-02-09 17:51:37', '2026-02-09 17:51:37');

-- ----------------------------
-- Table structure for seat_reservation
-- ----------------------------
DROP TABLE IF EXISTS `seat_reservation`;
CREATE TABLE `seat_reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seat_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `check_in_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_seat_reservation_seat`(`seat_id` ASC) USING BTREE,
  INDEX `idx_seat_reservation_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_seat_reservation_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_seat_reservation_status`(`status` ASC) USING BTREE
) AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seat_reservation
-- ----------------------------
INSERT INTO `seat_reservation` VALUES (20, 286, 1, '2026-02-09 02:21:00', '2026-02-09 06:21:00', 'RESERVED', NULL, '2026-02-09 18:21:24', '2026-02-09 18:21:24');
INSERT INTO `seat_reservation` VALUES (21, 285, 2, '2026-02-09 02:22:00', '2026-02-09 06:22:00', 'CANCELED', NULL, '2026-02-09 18:22:35', '2026-02-09 18:22:42');
INSERT INTO `seat_reservation` VALUES (22, 285, 1, '2026-02-09 03:35:00', '2026-02-09 07:35:00', 'RESERVED', NULL, '2026-02-09 19:35:20', '2026-02-09 19:35:20');

-- ----------------------------
-- Table structure for study_room
-- ----------------------------
DROP TABLE IF EXISTS `study_room`;
CREATE TABLE `study_room`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `building` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `floor` int NOT NULL,
  `area` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `total_seats` int NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_room
-- ----------------------------
INSERT INTO `study_room` VALUES (1, '第一自习室', '图书馆', 3, '东区', 25, 1, '2026-02-09 17:22:09', '2026-02-09 17:45:38');
INSERT INTO `study_room` VALUES (2, '第二自习室', '图书馆', 4, '西区', 5, 1, '2026-02-09 17:22:09', '2026-02-09 17:51:37');
INSERT INTO `study_room` VALUES (3, '第三自习室', '教学楼A', 2, '北区', 1, 1, '2026-02-09 17:22:09', '2026-02-09 17:45:15');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`config_key`) USING BTREE
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('reservation_rules', '{\"timeSlotMinutes\":30,\"advanceDays\":7,\"dailyLimit\":2,\"minDurationMinutes\":30,\"maxDurationMinutes\":180,\"approvalRequired\":true}', '2026-02-09 01:22:48');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_username`(`username` ASC) USING BTREE
) AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$fg71w/qiKsPQsgchQjVSde61zL8QUW7JjYFh1SGNX6UDu31mUlHM2', 'ADMIN', NULL, 1, '2026-02-09 02:07:36', '2026-02-09 02:07:36');
INSERT INTO `user` VALUES (2, 'student01', '$2a$10$jqoKXaearMweYRq31XT3fOAOQaLcTW8F9WYVCQPeZXgIk2JjGwUmm', 'STUDENT', NULL, 1, '2026-02-09 16:19:56', '2026-02-09 16:19:56');
INSERT INTO `user` VALUES (3, 'teacher1', '$2a$10$B8oR9GZquH9U/gPVQaavH.ESOrlcex8TvgKW55AMRalDoHFF.NGlu', 'TEACHER', NULL, 1, '2026-02-09 19:40:16', '2026-02-09 19:40:16');

SET FOREIGN_KEY_CHECKS = 1;
