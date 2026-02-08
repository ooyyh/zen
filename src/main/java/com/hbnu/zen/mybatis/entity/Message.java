package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long userId;
    private String templateCode;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
