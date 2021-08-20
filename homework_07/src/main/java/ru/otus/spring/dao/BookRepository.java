package ru.otus.spring.dao;

import java.util.List;

import ru.otus.spring.domain.Book;

public interface BookRepository {
    Long count();
    Book save(Book book);
    Book findById(long id);
    List<Book> findAll();
    void deleteById(long id);
    boolean isExistsById(long id);
}
