package ru.otus.spring.dao;

import ru.otus.spring.domain.Author;

public interface AuthorRepository {
    boolean isExistsById(long id);
    Author findById(long id);
}
