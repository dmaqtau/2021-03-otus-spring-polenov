package ru.otus.spring.dao;

import ru.otus.spring.domain.Genre;

public interface GenreRepository {
    boolean isExistsById(long id);
    Genre findById(long id);
}
