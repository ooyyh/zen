package com.hbnu.zen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusMessage {
    private Long seatId;
    private String status;  // "PENDING", "RESERVED", "AVAILABLE"
    private Long userId;
    private String username;  // 用户名
}
