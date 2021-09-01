package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тестирование репозитория для работы с жанрами")
@DataJpaTest
@Import({ GenreRepositoryJpa.class})
class GenreRepositoryTest {
    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long NOT_EXISTING_GENRE_ID = 2000L;
    private static final String EXISTING_GENRE_NAME = "Фэнтези";

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("Должна быть корректно выполнена проверка существования жанра")
    void shouldCheckGenreExistince(){
        assertTrue(genreRepository.isExistsById(EXISTING_GENRE_ID));
        assertFalse(genreRepository.isExistsById(NOT_EXISTING_GENRE_ID));
    }

    @Test
    @DisplayName("Должны корректно получить жанр по идентификатору")
    void shouldgetGenreById(){
        assertThat(genreRepository.findById(EXISTING_GENRE_ID)).isNotNull().hasFieldOrPropertyWithValue("name", EXISTING_GENRE_NAME);
        assertThat(genreRepository.findById(NOT_EXISTING_GENRE_ID)).isNull();
    }
}
