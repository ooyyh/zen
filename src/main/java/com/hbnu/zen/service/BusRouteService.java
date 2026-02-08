package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.mapper.BusRouteMapper;
import com.hbnu.zen.mybatis.entity.BusRoute;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusRouteService {
    private final BusRouteMapper busRouteMapper;

    public BusRouteService(BusRouteMapper busRouteMapper) {
        this.busRouteMapper = busRouteMapper;
    }

    public BusRoute create(BusRoute route) {
        if (route.getStatus() == null) {
            route.setStatus(1);
        }
        busRouteMapper.insert(route);
        return route;
    }

    public BusRoute update(BusRoute route) {
        BusRoute existing = busRouteMapper.selectById(route.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "线路不存在");
        }
        if (route.getStatus() == null) {
            route.setStatus(existing.getStatus());
        }
        busRouteMapper.update(route);
        return busRouteMapper.selectById(route.getId());
    }

    public BusRoute getById(Long id) {
        BusRoute route = busRouteMapper.selectById(id);
        if (route == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "线路不存在");
        }
        return route;
    }

    public List<BusRoute> list() {
        return busRouteMapper.selectAll();
    }
}
