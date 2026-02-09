-- Add real_name field to user table for registration feature

ALTER TABLE `user` ADD COLUMN `real_name` VARCHAR(100) NULL AFTER `role`;
