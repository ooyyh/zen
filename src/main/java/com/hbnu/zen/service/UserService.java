package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.mapper.UserMapper;
import com.hbnu.zen.mybatis.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号不存在或已禁用");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        return user;
    }

    public User createUser(String username, String password, String role) {
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    public User register(String username, String password, String role, String realName) {
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role != null ? role : "STUDENT");
        user.setStatus(1);
        if (realName != null && !realName.trim().isEmpty()) {
            user.setRealName(realName);
        }
        userMapper.insert(user);
        return user;
    }

    public List<User> listAll() {
        return userMapper.selectAll();
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
