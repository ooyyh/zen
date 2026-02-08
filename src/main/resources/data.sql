INSERT INTO sys_config (config_key, config_value)
VALUES (
  'reservation_rules',
  '{"timeSlotMinutes":30,"advanceDays":7,"dailyLimit":2,"minDurationMinutes":30,"maxDurationMinutes":180,"approvalRequired":true}'
)
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);

INSERT INTO message_template (template_code, title, content)
VALUES
  ('RESERVATION_CREATED', '预约提交成功', '你的预约申请已提交，等待审批。预约：{classroom} {startTime}-{endTime}'),
  ('RESERVATION_APPROVED', '预约已通过', '你的预约已通过审批。预约：{classroom} {startTime}-{endTime}'),
  ('RESERVATION_REJECTED', '预约被驳回', '你的预约被驳回。原因：{remark}'),
  ('LECTURE_SIGNUP_SUCCESS', '讲座报名成功', '你已报名讲座：{lectureTitle}，时间：{startTime}'),
  ('LECTURE_WAITLIST', '讲座候补提醒', '你已进入讲座候补队列：{lectureTitle}')
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content);
INSERT INTO message_template (template_code, title, content)
VALUES
  ('EQUIPMENT_BORROW_CREATED', '设备借用已提交', '你的设备借用申请已提交，等待审批。设备：{equipment} {startTime}-{endTime}'),
  ('EQUIPMENT_BORROW_APPROVED', '设备借用已通过', '你的设备借用已通过审批。设备：{equipment} {startTime}-{endTime}'),
  ('EQUIPMENT_BORROW_REJECTED', '设备借用被驳回', '你的设备借用被驳回。设备：{equipment} 原因：{remark}'),
  ('EQUIPMENT_BORROW_RETURNED', '设备归还确认', '设备归还已确认。设备：{equipment}')
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content);
INSERT INTO message_template (template_code, title, content)
VALUES
  ('BUS_BOOKED', '校车预约成功', '你已成功预约校车：{route} 发车时间 {departureTime}'),
  ('BUS_WAITLIST', '校车候补排队中', '你已进入校车候补队列：{route} 发车时间 {departureTime}'),
  ('BUS_CANCELED', '校车预约已取消', '你的校车预约已取消：{route} 发车时间 {departureTime}'),
  ('BUS_PROMOTED', '校车候补转正', '候补成功转正：{route} 发车时间 {departureTime}')
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content);
INSERT INTO message_template (template_code, title, content)
VALUES
  ('LECTURE_CHECKIN_SUCCESS', '讲座签到成功', '你已完成讲座签到：{lectureTitle} 时间 {startTime}')
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content);
