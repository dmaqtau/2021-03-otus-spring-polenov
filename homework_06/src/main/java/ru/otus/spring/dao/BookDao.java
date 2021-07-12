package ru.otus.spring.dao;

import ru.otus.spring.domain.Book;

import java.util.List;

public interface BookDao {
    Integer count();
    Book insert(Book book);
    Book update(Book book);
    Book getById(long id);
    List<Book> getAll();
    int deleteById(long id);
}
