package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username") String username);

    User selectById(@Param("id") Long id);

    List<User> selectAll();

    int insert(User user);
}
