package ru.otus.spring.service;

import ru.otus.spring.domain.Book;

import java.util.List;

public interface BookService {
    Book create(String bookName, String bookDescription, long authorId, long genreId);
    Book getByID(long id);
    List<Book> getAll();
    Book update(long id, String bookName, String bookDescription, long authorId, long genreId);
    int deleteByID(long id);
}
