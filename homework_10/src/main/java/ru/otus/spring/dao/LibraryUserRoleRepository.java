package ru.otus.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.domain.LibraryUserRole;

public interface LibraryUserRoleRepository extends JpaRepository<LibraryUserRole, Long> {
}
