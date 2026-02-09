package com.hbnu.zen.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SeatBatchCreateRequest {
    @NotNull(message = "生成模式不能为空")
    private String mode; // "grid" 或 "manual"

    // 网格模式参数
    @Min(value = 1, message = "行数至少为1")
    private Integer rows;

    @Min(value = 1, message = "列数至少为1")
    private Integer cols;

    private String prefix; // 座位编号前缀，如 "A"

    // 手动模式参数 - 单个座位信息数组（JSON传递）
    // 前端会发送包含 {seatNo, positionX, positionY, hasPower} 的数组
}
