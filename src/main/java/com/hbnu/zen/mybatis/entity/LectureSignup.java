package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LectureSignup {
    private Long id;
    private Long lectureId;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
}
