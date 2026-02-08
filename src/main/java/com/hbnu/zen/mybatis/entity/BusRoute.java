package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BusRoute {
    private Long id;
    private String name;
    private String origin;
    private String destination;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
