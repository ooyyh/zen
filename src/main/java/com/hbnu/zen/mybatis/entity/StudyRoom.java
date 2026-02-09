package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRoom {
    private Long id;
    private String name;
    private String building;
    private Integer floor;
    private String area;
    private Integer totalSeats;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
