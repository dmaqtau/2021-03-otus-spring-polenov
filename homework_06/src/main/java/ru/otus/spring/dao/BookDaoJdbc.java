package ru.otus.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.util.ValidationUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class BookDaoJdbc implements BookDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public BookDaoJdbc(NamedParameterJdbcOperations namedParameterJdbcOperations){
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public Integer count() {
        return namedParameterJdbcOperations.queryForObject("select count(*) as cnt from books", Map.of(), Integer.class);
    }

    @Override
    public Book insert(Book book) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValues(Map.of(
                "bookName", book.getBookName(),
                "description", book.getDescription(),
                "authorId", book.getAuthor().getId(),
                "genreId", book.getGenre().getId()
        ));

        final String sql = "insert into books (" +
                "book_name, description, author_id, genre_id" +
                ") " +
                "values (:bookName, :description, :authorId, :genreId)";

        namedParameterJdbcOperations.update(sql, parameterSource, keyHolder);

        Number key = keyHolder.getKey();
        Integer generatedId = key == null? null: key.intValue();
        return generatedId == null? null: getById(generatedId);
    }

    @Override
    public Book update(Book book) {
        Book existingBook = getById(book.getId());
        if(existingBook == null){
            throw new BookValidationException("Не существует книга с идентификатором " + book.getId());
        }

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValues(Map.of("id", book.getId()));

        List<String> fieldUpdateStatements = new ArrayList<>();

        if(book.getGenre() != null){
            fieldUpdateStatements.add("genre_id = :genreId");
            parameterSource.addValues(Map.of("genreId", book.getGenre().getId()));
        }
        if(book.getAuthor() != null){
            fieldUpdateStatements.add("author_id = :authorId");
            parameterSource.addValues(Map.of("authorId", book.getAuthor().getId()));
        }
        if(StringUtils.isNotBlank(book.getBookName())){
            fieldUpdateStatements.add("book_name = :bookName");
            parameterSource.addValues(Map.of("bookName", book.getBookName()));
        }

        if(StringUtils.isNotBlank(book.getDescription())){
            fieldUpdateStatements.add("description = :description");
            parameterSource.addValues(Map.of("description", book.getDescription()));
        }

        if(fieldUpdateStatements.isEmpty()){
            // Обновлять нечего, вернуть исходное состояние книги
            return existingBook;
        }

        String sql = "update books set " +
                String.join(", ", fieldUpdateStatements) + " " +
                "where id = :id";

        namedParameterJdbcOperations.update(sql, parameterSource);
        return getById(book.getId());
    }

    @Override
    public Book getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        try {
            return namedParameterJdbcOperations.queryForObject(
                    "SELECT b.id, b.book_name, b.description, a.id author_id, a.surname, " +
                            "a.author_name, a.patronymic,  g.id genre_id, g.genre_name " +
                            "FROM books b " +
                            "LEFT JOIN authors a ON b.author_id = a.id " +
                            "LEFT JOIN genres g ON b.genre_id = g.id WHERE b.id = :id", params, new BooksMapper());
        } catch (EmptyResultDataAccessException e){
            // Книг по заданному идентификатору в БД нет.
            return null;
        }
    }

    @Override
    public List<Book> getAll() {

        return namedParameterJdbcOperations.query(
                "SELECT b.id, b.book_name, b.description, a.id author_id, a.surname, " +
                        "a.author_name, a.patronymic,  g.id genre_id, g.genre_name " +
                        "FROM books b " +
                        "LEFT JOIN authors a ON b.author_id = a.id " +
                        "LEFT JOIN genres g ON b.genre_id = g.id", Map.of(), new BooksMapper());
    }

    @Override
    public int deleteById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        return namedParameterJdbcOperations.update(
                "delete from books where id = :id", params
        );
    }

    private static class BooksMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("book_name");
            String description = resultSet.getString("description");

            long authorId = resultSet.getLong("author_id");
            String authorName = resultSet.getString("author_name");
            String surname = resultSet.getString("surname");
            String patronymic = resultSet.getString("patronymic");

            long genreId = resultSet.getLong("genre_id");
            String genreName = resultSet.getString("genre_name");

            return Book.builder()
                    .id(id)
                    .bookName(name)
                    .description(description)
                    .author(new Author(authorId, surname, authorName, patronymic))
                    .genre(new Genre(genreId, genreName))
                    .build();
        }
    }
}
