package ru.otus.spring.dao;

import java.util.List;

import ru.otus.spring.domain.BookComment;

public interface BookCommentRepository {
    BookComment save(String userLogin, String comment, long bookId);
    List<BookComment> findByBookId(long bookId);
    void deleteById(long id);
    void deleteByBookId(long id);
    boolean isExistsById(long id);
}
