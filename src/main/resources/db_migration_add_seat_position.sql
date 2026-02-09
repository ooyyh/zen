-- 为座位表添加坐标字段，支持可视化布局
-- 创建时间: 2026-02-09
-- 说明: 添加座位坐标信息以支持Canvas可视化编辑

ALTER TABLE seat 
ADD COLUMN position_x INT COMMENT '座位X坐标（Canvas坐标）' AFTER seat_type,
ADD COLUMN position_y INT COMMENT '座位Y坐标（Canvas坐标）' AFTER position_x;

-- 更新说明：座位编号可以是A01, B02等格式
-- position_x, position_y 为Canvas画布上的坐标位置
-- 自动生成模式：坐标会按行列自动计算
-- 手动拖拽模式：坐标由用户拖拽确定
