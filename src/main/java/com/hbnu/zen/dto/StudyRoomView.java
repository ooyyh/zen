package com.hbnu.zen.dto;

import lombok.Data;

@Data
public class StudyRoomView {
    private Long id;
    private String name;
    private String building;
    private Integer floor;
    private String area;
    private Integer totalSeats;
    private Integer availableSeats;
}
