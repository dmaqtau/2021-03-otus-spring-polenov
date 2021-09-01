package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тестирование репозитория для работы с авторами")
@DataJpaTest
class AuthorRepositoryTest {

    private static final Long EXISTING_AUTHOR_ID = 3L;
    private static final Long NOT_EXISTING_AUTHOR_ID = 2000L;
    private static final String EXISTING_AUTHOR_NAME = "Зиро";

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("Должна быть корректно выполнена проверка существования автора")
    void shouldCheckAuthorExistince(){
        assertTrue(authorRepository.existsById(EXISTING_AUTHOR_ID));
        assertFalse(authorRepository.existsById(NOT_EXISTING_AUTHOR_ID));
    }

    @Test
    @DisplayName("Должны корректно получить автора по идентификатору")
    void shouldgetAuthorById(){
        assertThat(authorRepository.findById(EXISTING_AUTHOR_ID).orElse(null))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", EXISTING_AUTHOR_NAME);
        assertThat(authorRepository.findById(NOT_EXISTING_AUTHOR_ID)).isEmpty();
    }
}
