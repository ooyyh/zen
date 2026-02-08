package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Equipment {
    private Long id;
    private String name;
    private String category;
    private String assetNo;
    private String location;
    private Integer totalQty;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
