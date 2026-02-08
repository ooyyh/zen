package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Classroom {
    private Long id;
    private String building;
    private String roomNo;
    private Integer capacity;
    private String location;
    private String equipmentJson;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
