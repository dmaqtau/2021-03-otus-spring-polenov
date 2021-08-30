package ru.otus.spring.service;

import java.util.List;

import ru.otus.spring.domain.BookComment;
import ru.otus.spring.dto.BookCommentDTO;

public interface BookCommentService {
    List<BookComment> findComments(long bookId);
    BookComment addComment(BookCommentDTO commentDTO);
    void deleteCommentById(long commentId);
    void deleteCommentsByBookId(long bookId);
}
