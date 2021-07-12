package ru.otus.spring.dao;

import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Author;

import java.util.List;

@Repository
public class AuthorDaoJdbc implements AuthorDao{
    @Override
    public List<Author> getAll() {
        return null;
    }
}
