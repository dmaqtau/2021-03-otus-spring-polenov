package ru.otus.spring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(value = "bookWithAuthorAndGenre")
    List<Book> findAll();
}
