package com.hbnu.zen.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EquipmentCreateRequest {
    @NotBlank(message = "设备名称不能为空")
    private String name;
    private String category;
    private String assetNo;
    private String location;
    @NotNull(message = "数量不能为空")
    private Integer totalQty;
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
