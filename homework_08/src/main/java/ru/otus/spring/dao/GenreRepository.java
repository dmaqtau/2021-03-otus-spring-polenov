package ru.otus.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
