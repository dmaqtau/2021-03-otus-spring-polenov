package ru.otus.spring.dao;

import java.util.List;

import ru.otus.spring.domain.Book;

public interface BookDao {
    Integer count();
    Book insert(Book book);
    Book update(Book book);
    Book getById(long id);
    List<Book> getAll();
    int deleteById(long id);
}
