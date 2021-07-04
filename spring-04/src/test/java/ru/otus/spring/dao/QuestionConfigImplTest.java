package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметры конфига парсятся из application-test.yml в папке тестовых ресурсов
 */
@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
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
