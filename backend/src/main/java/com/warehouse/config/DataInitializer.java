package com.warehouse.config;

import com.warehouse.entity.*;
import com.warehouse.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(null) > 0) {
            log.info("Database already initialized, skipping seed data");
            return;
        }

        log.info("Initializing seed data...");

        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setRoleName("超级管理员");
        adminRole.setRoleCode("ROLE_ADMIN");
        adminRole.setDescription("系统最高权限");
        roleMapper.insert(adminRole);

        Role whRole = new Role();
        whRole.setId(2L);
        whRole.setRoleName("仓库管理员");
        whRole.setRoleCode("ROLE_WAREHOUSE");
        whRole.setDescription("仓库管理权限");
        roleMapper.insert(whRole);

        Role userRole = new Role();
        userRole.setId(3L);
        userRole.setRoleName("普通用户");
        userRole.setRoleCode("ROLE_USER");
        userRole.setDescription("基本查看权限");
        roleMapper.insert(userRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNickname("系统管理员");
        admin.setEmail("admin@warehouse.com");
        admin.setStatus(1);
        userMapper.insert(admin);

        UserRole ur = new UserRole();
        ur.setUserId(1L);
        ur.setRoleId(1L);
        userRoleMapper.insert(ur);

        log.info("Seed data initialized: admin/admin123 with ROLE_ADMIN");
    }
}
