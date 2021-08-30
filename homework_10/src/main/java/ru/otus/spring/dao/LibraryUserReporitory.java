package ru.otus.spring.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.domain.LibraryUser;

public interface LibraryUserReporitory extends JpaRepository<LibraryUser, Long> {
    Optional<LibraryUser> findByLogin(String name);
}
