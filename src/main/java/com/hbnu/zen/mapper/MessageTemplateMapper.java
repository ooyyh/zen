package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.MessageTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageTemplateMapper {
    MessageTemplate selectByCode(@Param("templateCode") String templateCode);
}
