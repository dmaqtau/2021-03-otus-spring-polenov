package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-config.properties")
public class QuestionConfigImplTest {
    @Autowired
    private QuestionConfig questionConfig;

    private static final int ANSWER_QUALIFY_QTY = 3;

    @DisplayName("Должны вернуться корректные параметры вопросов")
    @Test
    void shouldReturnCorrectParamsForQuestions(){
        assertThat(questionConfig.getPassAnswerQty()).isEqualTo(ANSWER_QUALIFY_QTY);
    }
}
