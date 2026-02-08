package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.BusRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusRouteMapper {
    int insert(BusRoute route);

    int update(BusRoute route);

    BusRoute selectById(@Param("id") Long id);

    List<BusRoute> selectAll();
}
