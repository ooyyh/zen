package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message message);

    Message selectById(@Param("id") Long id);

    List<Message> selectByUser(@Param("userId") Long userId);

    int markRead(@Param("id") Long id,
                 @Param("readAt") java.time.LocalDateTime readAt);

    int countUnread(@Param("userId") Long userId);
}
