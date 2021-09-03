package ru.otus.spring.service;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.domain.Question;
import ru.otus.spring.util.Const;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class QuestionValidationServiceImplTest {
    @MockBean
    private LocaleService localeService;
    @MockBean
    private IOService ioService;
    @Autowired
    private QuestionValidationServiceImpl questionValidationService;

    @Captor
    private ArgumentCaptor<String> outArgCaptor;

    private static final String EMPTY_QUESTIONS_LIST_MSG = "empty_questions_list_msg";
    private static final String CORRUPT_QUESTIONS_LIST_MSG = "corrupt_questions_list_msg";

    @DisplayName("Должно быть выдано сообщение об отсутствии вопросов")
    @Test
    void shoundWarnAboutNoQuestions() {
        given(localeService.getLocalizedString(Const.STRINGS_EMPTY_MESSAGE_LIST)).willReturn(EMPTY_QUESTIONS_LIST_MSG);

        assertAll(
                 () -> assertThat(questionValidationService.validateQuestions(null)).isEmpty(),
                 () -> verify(ioService, times(1)).out(outArgCaptor.capture())
        );

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(0)).contains(EMPTY_QUESTIONS_LIST_MSG);
    }

    @DisplayName("Должно быть выдано сообщение об отсутствии валидных вопросов")
    @Test
    void shoundWarnAboutNoValidQuestions() {
        given(localeService.getLocalizedString(Const.STRINGS_NO_VALID_QUESTIONS)).willReturn(CORRUPT_QUESTIONS_LIST_MSG);

        List<Question> corruptQuestions = List.of(new Question());

        assertAll(
                () -> assertThat(questionValidationService.validateQuestions(corruptQuestions)).isEmpty(),
                () -> verify(ioService, times(1)).out(outArgCaptor.capture())
        );

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(0)).contains(CORRUPT_QUESTIONS_LIST_MSG);
    }
}
