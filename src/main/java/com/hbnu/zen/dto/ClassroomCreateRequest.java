package com.hbnu.zen.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ClassroomCreateRequest {
    @NotBlank(message = "楼栋不能为空")
    private String building;
    @NotBlank(message = "教室号不能为空")
    private String roomNo;
    @NotNull(message = "容量不能为空")
    private Integer capacity;
    private String location;
    private String equipmentJson;
    private Integer status;

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEquipmentJson() {
        return equipmentJson;
    }

    public void setEquipmentJson(String equipmentJson) {
        this.equipmentJson = equipmentJson;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
