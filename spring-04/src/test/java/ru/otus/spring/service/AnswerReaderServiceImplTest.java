package ru.otus.spring.service;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.domain.Question;
import ru.otus.spring.util.Const;
import ru.otus.spring.utils.QuestionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AnswerReaderServiceImplTest {
    @MockBean
    private LocaleService localeService;
    @MockBean
    private IOService ioService;
    @Autowired
    private AnswerReaderService answerReaderService;

    private static final int ANSWER_OPTION_COUNT = 3;
    private static final String WRONG_ANSWER_OPTION_MSG = "wrong_answer_option_msg";
    private static final String WRONG_ANSWER_INPUT_MSG = "wrong_answer_input_msg";

    @Test
    @DisplayName("Должен вернуть сообщение о неверном вводе ответа")
    void shouldReturnWrongAnswerInputNotification() throws IOException {
        List<Question> questions = QuestionUtils.getSingleTestQuestion(ANSWER_OPTION_COUNT, 0);

        when(localeService.getLocalizedString(eq(Const.STRINGS_WRONG_ANSWER_OPTION), anyString())).thenReturn(WRONG_ANSWER_OPTION_MSG);
        when(localeService.getLocalizedString(Const.STRINGS_WRONG_ANSWER_INPUT)).thenReturn(WRONG_ANSWER_INPUT_MSG);

        assertSingleWrongGivenAnswer(questions, "100", WRONG_ANSWER_INPUT_MSG);
        assertSingleWrongGivenAnswer(questions, "z", WRONG_ANSWER_OPTION_MSG);
        assertSingleWrongGivenAnswer(questions, "abc", WRONG_ANSWER_INPUT_MSG);
    }

    private void assertSingleWrongGivenAnswer(List<Question> questions, String givenAnswer, String expectedMessage) throws IOException {
        Mockito.reset(ioService);

        given(ioService.readLine())
                .willReturn(givenAnswer)      // Заведомо неверный индекс опции ответа
                .willReturn("1");             // Далее отвечаем правильно, чтобы не уйти в бесконечный цикл

        assertAll(
                () -> assertThat(answerReaderService.getUserAnswer(ANSWER_OPTION_COUNT).getErrNotification()).isEqualTo(expectedMessage),
                () -> verify(ioService).readLine()
        );
    }
}
