package ru.otus.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.domain.BookComment;

public interface BookCommentRepository extends JpaRepository<BookComment, Long> {
    List<BookComment> findAllByBookId(Long bookId);
    void deleteAllByBookId(Long bookId);
}
