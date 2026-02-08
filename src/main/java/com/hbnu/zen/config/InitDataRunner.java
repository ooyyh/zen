package com.hbnu.zen.config;

import com.hbnu.zen.common.Role;
import com.hbnu.zen.mapper.UserMapper;
import com.hbnu.zen.mybatis.entity.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitDataRunner implements ApplicationRunner {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public InitDataRunner(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        User existing = userMapper.selectByUsername("admin");
        if (existing == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(1);
            userMapper.insert(admin);
        }
    }
}
