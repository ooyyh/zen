package com.hbnu.zen.dto;

import lombok.Data;

@Data
public class SeatView {
    private Long id;
    private Long studyRoomId;
    private String seatNo;
    private String seatType;
    private Integer hasPower;
    private Boolean reserved;
}
