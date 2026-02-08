package com.hbnu.zen.dto;

import javax.validation.constraints.NotBlank;

public class BusRouteCreateRequest {
    @NotBlank(message = "线路名称不能为空")
    private String name;
    @NotBlank(message = "起点不能为空")
    private String origin;
    @NotBlank(message = "终点不能为空")
    private String destination;
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
