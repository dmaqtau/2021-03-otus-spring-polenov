package ru.otus.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static ru.otus.spring.security.UserRole.ADMIN;
import static ru.otus.spring.security.UserRole.MANAGER;
import static ru.otus.spring.security.UserRole.USER;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${api.book.path}")
    private String apiBookPath;

    private UserDetailsService userDetailsService;

    public SecurityConfiguration(UserDetailsService userService) {
        this.userDetailsService = userService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/about").hasAnyRole(ADMIN, MANAGER, USER)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, apiBookPath).hasAnyRole(ADMIN, MANAGER, USER)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, apiBookPath).hasAnyRole(ADMIN, MANAGER)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.PUT, apiBookPath).hasAnyRole(ADMIN, MANAGER)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.DELETE, apiBookPath).hasAnyRole(ADMIN, MANAGER)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/swagger-*/**").hasRole(ADMIN)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/v3/**").hasRole(ADMIN)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/admin/**").hasRole(ADMIN)
                .and()
                .exceptionHandling().accessDeniedPage("/not_authorized")
                .and()
                .authorizeRequests().antMatchers("/**").denyAll().and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
