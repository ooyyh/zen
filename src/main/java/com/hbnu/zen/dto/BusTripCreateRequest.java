package com.hbnu.zen.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BusTripCreateRequest {
    @NotNull(message = "线路不能为空")
    private Long routeId;
    private String busNo;
    @NotNull(message = "发车时间不能为空")
    private LocalDateTime departureTime;
    @NotNull(message = "到达时间不能为空")
    private LocalDateTime arrivalTime;
    @NotNull(message = "座位数不能为空")
    private Integer capacity;
    private String status;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
