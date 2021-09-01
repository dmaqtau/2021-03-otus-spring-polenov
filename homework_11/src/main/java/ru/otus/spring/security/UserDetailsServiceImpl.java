package ru.otus.spring.security;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LibraryUser dbUser = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Library user not found by name: " + username));

        return User.builder()
                .password(dbUser.getPassword())
                .username(dbUser.getLogin())
                .disabled(!dbUser.isActive())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .authorities(getAuthorities(dbUser))
                .build();
    }

    private Set<GrantedAuthority> getAuthorities(LibraryUser dbUser){
        if(dbUser.getUserRoles() == null){
            return Set.of();
        }
        return dbUser.getUserRoles().stream()
                .map(r -> new SimpleGrantedAuthority(ROLE_PREFIX + r.getRole()))
                .collect(Collectors.toSet());
    }
}
