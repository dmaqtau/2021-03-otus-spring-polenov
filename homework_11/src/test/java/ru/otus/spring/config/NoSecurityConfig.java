package ru.otus.spring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
public class NoSecurityConfig implements WebSecurityConfigurer<WebSecurity> {
    @Override
    public void init(WebSecurity builder) throws Exception {
        builder.ignoring().requestMatchers(
                new AntPathRequestMatcher("/**"));
    }

    @Override
    public void configure(WebSecurity builder) throws Exception {
    }
}
