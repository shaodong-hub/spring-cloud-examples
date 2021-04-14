package com.github.spring.cloud.user.center.config;

import com.github.spring.cloud.user.center.manager.IRoleDetailManager;
import com.github.spring.cloud.user.center.pojo.entity.SystemMidUserRoleDO;
import com.github.spring.cloud.user.center.pojo.entity.SystemRoleDO;
import com.github.spring.cloud.user.center.pojo.entity.SystemUserDO;
import com.github.spring.cloud.user.center.repository.ISystemMidUserRoleRepository;
import com.github.spring.cloud.user.center.repository.ISystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Collections;

/**
 * <p>
 * 创建时间为 下午4:47 2020/5/10
 * 项目名称 spring-boot-security
 * </p>
 *
 * @author 石少东
 * @version 0.0.1
 * @since 0.0.1
 */

@Slf4j
@Order(1)
@Configuration
@RequiredArgsConstructor
public class SystemContextInitializerConfig implements CommandLineRunner {

    private final PropertiesConfig propertiesConfig;

    private final ISystemUserRepository userRepository;

    private final IRoleDetailManager roleRepository;

    private final ISystemMidUserRoleRepository userRoleRepository;

    private static final String ROOT_CODE = "ROLE_ROOT";

    private static final String PASSWORD = "123456";

    @Override
    public void run(String... args) {
        saveUserIfNotExist(saveRoleIfNotExist());
    }

    private SystemRoleDO saveRoleIfNotExist() {
        if (!roleRepository.existsByRoleCode(ROOT_CODE)) {
            SystemRoleDO role = roleRepository.save(getRole());
            log.debug("初始化角色:{}", role);
            return role;
        }
        return roleRepository.findByRoleCode(ROOT_CODE);
    }

    private void saveUserIfNotExist(SystemRoleDO role) {
        SystemUserDO user;
        if (!userRepository.existsByUsername(propertiesConfig.getUsername())) {
            user = getUser();
            user.setRoles(Collections.singleton(role));
            user = userRepository.save(getUser());
            log.debug("初始化用户:{}", user);
        } else {
            user = userRepository.findById(1L);
        }
        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            SystemMidUserRoleDO userRole = SystemMidUserRoleDO.builder().userId(user.getId()).roleId(role.getId()).build();
            userRoleRepository.save(userRole);
            log.debug("用户赋角色:{}", userRole);
        }
    }

    private SystemRoleDO getRole() {
        return SystemRoleDO.builder().roleName("超管").roleCode(ROOT_CODE).build();
    }

    private SystemUserDO getUser() {
        return SystemUserDO.builder()
                .username(propertiesConfig.getUsername())
                .mobile(propertiesConfig.getMobile())
                .email(propertiesConfig.getEmail())
                .password(PASSWORD).build();
    }

}
