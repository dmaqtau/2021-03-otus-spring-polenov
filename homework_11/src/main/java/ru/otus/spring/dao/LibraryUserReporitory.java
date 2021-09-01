package ru.otus.spring.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.domain.LibraryUser;

public interface LibraryUserReporitory extends JpaRepository<LibraryUser, Long> {
    @EntityGraph(value = "LibraryUserInfo")
    Optional<LibraryUser> findByLogin(String name);
}
