package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageTemplate {
    private Long id;
    private String templateCode;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
