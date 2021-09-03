package ru.otus.spring.config;

import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.otus.spring.security.UserRole;

@TestConfiguration
public class TestSpringSecurityConfig {
    public static final String ADMIN_LOGIN = "activeuser";
    public static final String INACTIVE_USER_LOGIN = "inactiveuser";
    public static final String MANAGER_LOGIN = "manager";
    public static final String COMMON_USER_LOGIN = "commonuser";

    public static final String ADMIN_PASS = "pepper";
    public static final String INACTIVE_USER_PASS = "pepper1";

    private static final String ACTIVE_USER_PASS_HASHED = "$2a$10$wQcUhlu1q8brE2zFv8fBxu/l/vTXRWrz.qCzPWEMX./6nhHBxsCKe";       // Исходный пароль: 'pepper'
    private static final String INACTIVE_USER_PASS_HASHED = "$2a$10$cOtOBzl57tw8hnWieKdhOOn5O8bE1MLA6J6EtX7Q1hOJFD7ILz/LW";     // Исходный пароль: 'pepper1'
    private static final String MANAGER_USER_PASS_HASHED = "$2a$10$.3aHA0sEUOwqe.D0.qZpiONMT5P6BZnwOWQPyxzXmxmOi/W57M3Z6";      // Исходный пароль: 'pepper2'
    private static final String COMMON_USER_PASS_HASHED = "$2a$10$oQMO6WcnmASv.YpZsMGWmOMNroPSbXbV9ilAduPx9ew525whCihrG";       // Исходный пароль: 'pepper3'

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService(){
        User activeUser = new User(
                ADMIN_LOGIN, ACTIVE_USER_PASS_HASHED, true, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.ADMIN)));
        User inactiveUser = new User(
                INACTIVE_USER_LOGIN, INACTIVE_USER_PASS_HASHED, false, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.ADMIN)));
        User manager = new User(
                MANAGER_LOGIN, MANAGER_USER_PASS_HASHED, true, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.MANAGER)));
        User commonUser = new User(
                COMMON_USER_LOGIN, COMMON_USER_PASS_HASHED, true, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.USER)));
        return new InMemoryUserDetailsManager(List.of(activeUser, inactiveUser, manager, commonUser));
    }
}
