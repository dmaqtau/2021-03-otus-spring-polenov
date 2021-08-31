package ru.otus.spring.security;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static ru.otus.spring.security.UserRole.ADMIN;
import static ru.otus.spring.security.UserRole.MANAGER;
import static ru.otus.spring.security.UserRole.USER;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${api.book.path}")
    private String apiBookPath;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/about").hasAnyRole(roles(ADMIN, MANAGER, USER))
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, apiBookPath).hasAnyRole(roles(ADMIN, MANAGER, USER))
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, apiBookPath).hasAnyRole(roles(ADMIN, MANAGER))
                .and()
                .authorizeRequests().antMatchers(HttpMethod.PUT, apiBookPath).hasAnyRole(roles(ADMIN, MANAGER))
                .and()
                .authorizeRequests().antMatchers(HttpMethod.DELETE, apiBookPath).hasAnyRole(roles(ADMIN, MANAGER))
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/swagger-*/**").hasRole(ADMIN.getName())
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/v3/**").hasRole(ADMIN.getName())
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/admin/**").hasRole(ADMIN.getName())
                .and()
                .formLogin().permitAll().defaultSuccessUrl("/swagger-ui/")
                .and()
                .exceptionHandling().accessDeniedPage("/not_authorized")
                .and()
                .authorizeRequests().antMatchers("/**").denyAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static String[] roles(UserRole... userRoles){
        return Stream.of(userRoles).map(UserRole::getName).toArray(String[]::new);
    }
}
