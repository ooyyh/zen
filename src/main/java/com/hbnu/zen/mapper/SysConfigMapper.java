package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysConfigMapper {
    SysConfig selectByKey(@Param("configKey") String configKey);

    int upsert(SysConfig config);
}
