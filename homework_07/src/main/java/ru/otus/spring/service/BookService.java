package ru.otus.spring.service;

import java.util.List;

import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;

public interface BookService {
    Book create(String bookName, String bookDescription, long authorId, long genreId);
    Book findByID(long id);
    List<Book> getAll();
    Book update(long id, String bookName, String bookDescription, long authorId, long genreId);
    void deleteByID(long id);
    List<BookComment> findComments(long bookId);
    BookComment addComment(long bookId, String userLogin, String comment);
    void deleteCommentById(long commentId);
    void deleteCommentByBookId(long bookId);
}
