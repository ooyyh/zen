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
