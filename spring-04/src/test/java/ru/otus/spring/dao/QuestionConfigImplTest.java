package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметры конфига парсятся из application-test.yml в папке тестовых ресурсов
 */
@SpringBootTest
class QuestionConfigImplTest {
    @Autowired
    private QuestionConfig questionConfig;

    private static final int ANSWER_QUALIFY_QTY = 3;

    @DisplayName("Должны вернуться корректные параметры вопросов")
    @Test
    void shouldReturnCorrectParamsForQuestions(){
        assertThat(questionConfig.getQualifyAnswerQty()).isEqualTo(ANSWER_QUALIFY_QTY);
    }
}
