package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.BusTripView;
import com.hbnu.zen.mybatis.entity.BusTrip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusTripMapper {
    int insert(BusTrip trip);

    int update(BusTrip trip);

    BusTrip selectById(@Param("id") Long id);

    List<BusTrip> selectAll(@Param("status") String status);

    List<BusTripView> selectViews(@Param("status") String status);

    int countAll();
}
