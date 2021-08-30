package ru.otus.spring.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestSpringSecurityConfig {
    public static final String ACTIVE_USER_LOGIN = "activeuser";
    public static final String INACTIVE_USER_LOGIN = "inactiveuser";
    public static final String NOT_EXISTING_USER_LOGIN = "notexistinguser";

    public static final String ACTIVE_USER_PASS = "pepper";
    public static final String INACTIVE_USER_PASS = "pepper1";

    private static final String ACTIVE_USER_PASS_HASHED = "$2a$10$wQcUhlu1q8brE2zFv8fBxu/l/vTXRWrz.qCzPWEMX./6nhHBxsCKe";       // Исходный пароль: 'pepper'
    private static final String INACTIVE_USER_PASS_HASHED = "$2a$10$cOtOBzl57tw8hnWieKdhOOn5O8bE1MLA6J6EtX7Q1hOJFD7ILz/LW";     // Исходный пароль: 'pepper1'

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService(){
        User activeUser = new User(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS_HASHED, true, true, true, true, new ArrayList<>());
        User inactiveUser = new User(INACTIVE_USER_LOGIN, INACTIVE_USER_PASS_HASHED, false, true, true, true, new ArrayList<>());

        return new InMemoryUserDetailsManager(Arrays.asList(activeUser, inactiveUser));
    }
}
