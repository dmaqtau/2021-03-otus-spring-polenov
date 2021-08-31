package ru.otus.spring.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dao.LibraryUserReporitory;
import ru.otus.spring.dao.LibraryUserRoleRepository;
import ru.otus.spring.domain.LibraryUser;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final LibraryUserReporitory userRepository;
    private final LibraryUserRoleRepository roleRepository;

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        LibraryUser dbUser = userRepository.findByLogin(s)
                .orElseThrow(() -> new UsernameNotFoundException("Library user not found by name: " + s));

        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                if(dbUser.getUserRoles() == null){
                    return Set.of();
                }
                return dbUser.getUserRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(ROLE_PREFIX + r.getRole().getName()))
                        .collect(Collectors.toSet());
            }

            @Override
            public String getPassword() {
                return dbUser.getPassword();
            }

            @Override
            public String getUsername() {
                return dbUser.getLogin();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return dbUser.isActive();
            }
        };
    }
}
