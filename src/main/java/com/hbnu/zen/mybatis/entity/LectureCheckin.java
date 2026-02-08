package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LectureCheckin {
    private Long id;
    private Long lectureId;
    private Long userId;
    private LocalDateTime checkInAt;
    private LocalDateTime createdAt;
}
