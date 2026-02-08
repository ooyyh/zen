package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.MessageTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageTemplateMapper {
    MessageTemplate selectByCode(@Param("templateCode") String templateCode);

    MessageTemplate selectById(@Param("id") Long id);

    java.util.List<MessageTemplate> selectAll();

    int insert(MessageTemplate template);

    int update(MessageTemplate template);
}
