package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource("classpath:test-config.properties")
class QuestionConfigImplTest {

    private static final int ANSWER_QUALIFY_QTY = 3;

    @DisplayName("Должны вернуться корректные параметры вопросов")
    @Test
    void shouldReturnCorrectParamsForQuestions(){
        QuestionConfigImpl questionConfig = new QuestionConfigImpl();
        questionConfig.setPassAnswerQty(ANSWER_QUALIFY_QTY);
        assertThat(questionConfig.getPassAnswerQty()).isEqualTo(ANSWER_QUALIFY_QTY);
    }
}
