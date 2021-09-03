package ru.otus.spring.service;

import java.util.List;

import ru.otus.spring.domain.Book;
import ru.otus.spring.dto.BookDTO;

public interface BookService {
    List<Book> getAll();
    Book findByID(long id);
    Book create(BookDTO bookDTO);
    Book update(BookDTO bookDTO);
    void deleteByID(long id);

    void checkBookExists(long bookId);

}
