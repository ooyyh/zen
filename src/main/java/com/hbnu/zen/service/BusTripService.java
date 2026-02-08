package com.hbnu.zen.service;

import com.hbnu.zen.common.BusTripStatus;
import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.dto.BusTripCreateRequest;
import com.hbnu.zen.dto.BusTripView;
import com.hbnu.zen.mapper.BusRouteMapper;
import com.hbnu.zen.mapper.BusTripMapper;
import com.hbnu.zen.mybatis.entity.BusRoute;
import com.hbnu.zen.mybatis.entity.BusTrip;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BusTripService {
    private final BusTripMapper busTripMapper;
    private final BusRouteMapper busRouteMapper;

    public BusTripService(BusTripMapper busTripMapper, BusRouteMapper busRouteMapper) {
        this.busTripMapper = busTripMapper;
        this.busRouteMapper = busRouteMapper;
    }

    public BusTrip create(BusTripCreateRequest request) {
        validate(request);
        BusRoute route = busRouteMapper.selectById(request.getRouteId());
        if (route == null || route.getStatus() == null || route.getStatus() == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "线路不可用");
        }
        BusTrip trip = new BusTrip();
        trip.setRouteId(request.getRouteId());
        trip.setBusNo(request.getBusNo());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setArrivalTime(request.getArrivalTime());
        trip.setCapacity(request.getCapacity());
        trip.setStatus(request.getStatus() == null ? BusTripStatus.OPEN : request.getStatus());
        busTripMapper.insert(trip);
        return trip;
    }

    public BusTrip update(Long id, BusTripCreateRequest request) {
        validate(request);
        BusTrip existing = busTripMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班次不存在");
        }
        BusTrip trip = new BusTrip();
        trip.setId(id);
        trip.setRouteId(request.getRouteId());
        trip.setBusNo(request.getBusNo());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setArrivalTime(request.getArrivalTime());
        trip.setCapacity(request.getCapacity());
        trip.setStatus(request.getStatus() == null ? existing.getStatus() : request.getStatus());
        busTripMapper.update(trip);
        return busTripMapper.selectById(id);
    }

    public BusTrip getById(Long id) {
        BusTrip trip = busTripMapper.selectById(id);
        if (trip == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班次不存在");
        }
        return trip;
    }

    public List<BusTrip> list(String status) {
        return busTripMapper.selectAll(status);
    }

    public List<BusTripView> listViews(String status) {
        return busTripMapper.selectViews(status);
    }

    private void validate(BusTripCreateRequest request) {
        LocalDateTime dep = request.getDepartureTime();
        LocalDateTime arr = request.getArrivalTime();
        if (arr.isBefore(dep) || arr.isEqual(dep)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "到达时间必须晚于发车时间");
        }
        if (request.getCapacity() <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "座位数必须大于0");
        }
    }
}
