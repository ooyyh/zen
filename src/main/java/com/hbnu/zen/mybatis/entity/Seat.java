package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Seat {
    private Long id;
    private Long studyRoomId;
    private String seatNo;
    private String seatType;
    private Integer hasPower;
    private Integer positionX;
    private Integer positionY;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
