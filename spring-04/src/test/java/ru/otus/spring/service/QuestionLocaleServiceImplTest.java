package ru.otus.spring.service;

import java.util.Locale;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
class QuestionLocaleServiceImplTest {
    @Mock
    MessageSource messageSource;
    @InjectMocks
    QuestionLocaleServiceImpl questionLocaleService;

    private static String LOCALE_RU = "ru";
    private static String LOCALE_DEFAULT = "";

    private static String TEST_WELCOME_MSG_RU = "test_welcome_msg_ru";
    private static String TEST_WELCOME_MSG = "test_welcome_msg";

    private static String TEST_RESULT_MSG_RU = "test_result_msg_ru";
    private static String TEST_RESULT_MSG = "test_result_msg";

    private static String TEST_WRONG_ANSWER_INPUT_MSG_RU = "test_wrong_answer_input_msg_ru";
    private static String TEST_WRONG_ANSWER_INPUT_MSG = "test_wrong_answer_input_msg";

    private static String TEST_WRONG_ANSWER_OPTION_MSG_RU = "test_wrong_answer_option_msg_ru";
    private static String TEST_WRONG_ANSWER_OPTION_MSG = "test_wrong_answer_option_msg";

    private static String TEST_NO_VALID_QUESTIONS_MSG_RU = "test_no_valid_questions_msg_ru";
    private static String TEST_NO_VALID_QUESTIONS_MSG = "test_no_valid_questions_msg";

    private static String TEST_EMPTY_QUESTIONS_MSG_RU = "test_empty_questions_msg_ru";
    private static String TEST_EMPTY_QUESTIONS_MSG = "test_empty_questions_msg";

    private static String TEST_QUESTIONS_ASK_MSG_RU = "test_questions_ask_msg_ru";
    private static String TEST_QUESTIONS_ASK_MSG = "test_questions_ask_msg";

    private static String TEST_ANSWER_ERR_MSG_RU = "test_answer_err_msg_ru";
    private static String TEST_ANSWER_ERR_MSG = "test_answer_err_msg";

    private static String TEST_ABOUT_MSG_RU = "test_about_msg_ru";
    private static String TEST_ABOUT_MSG = "test_about_msg";

    @Test
    @DisplayName("Должны получить локализованное сообщение приветствия")
    void shouldGetLocalizedWelcomeMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_WELCOME), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_WELCOME_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_WELCOME), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_WELCOME_MSG);

        setLocale(LOCALE_RU);
        String welcomeMessage = questionLocaleService.getWelcomeMessage();
        assertThat(welcomeMessage).isEqualTo(TEST_WELCOME_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        welcomeMessage = questionLocaleService.getWelcomeMessage();
        assertThat(welcomeMessage).isEqualTo(TEST_WELCOME_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение о некорректном вводе ответа")
    void shouldGetLocalizedWrongAnswerInputMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_WRONG_ANSWER_INPUT), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_WRONG_ANSWER_INPUT_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_WRONG_ANSWER_INPUT), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_WRONG_ANSWER_INPUT_MSG);

        setLocale(LOCALE_RU);
        String welcomeMessage = questionLocaleService.getWrongAnswerInputMessage();
        assertThat(welcomeMessage).isEqualTo(TEST_WRONG_ANSWER_INPUT_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        welcomeMessage = questionLocaleService.getWrongAnswerInputMessage();
        assertThat(welcomeMessage).isEqualTo(TEST_WRONG_ANSWER_INPUT_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение о некорректной выбранной опции ответа")
    void shouldGetLocalizedWrongAnswerOptionMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_WRONG_ANSWER_OPTION), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_WRONG_ANSWER_OPTION_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_WRONG_ANSWER_OPTION), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_WRONG_ANSWER_OPTION_MSG);

        setLocale(LOCALE_RU);
        String wrongAnswerOptionMessage = questionLocaleService.getWrongAnswerOptionMessage(1);
        assertThat(wrongAnswerOptionMessage).isEqualTo(TEST_WRONG_ANSWER_OPTION_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        wrongAnswerOptionMessage = questionLocaleService.getWrongAnswerOptionMessage(1);
        assertThat(wrongAnswerOptionMessage).isEqualTo(TEST_WRONG_ANSWER_OPTION_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение об ошибке получения ответа на вопрос")
    void shouldGetLocalizedAnswerErrorMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_ANSWER_ERROR), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_ANSWER_ERR_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_ANSWER_ERROR), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_ANSWER_ERR_MSG);

        setLocale(LOCALE_RU);
        String answerErrMessage = questionLocaleService.getAnswerErrorMessage();
        assertThat(answerErrMessage).isEqualTo(TEST_ANSWER_ERR_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        answerErrMessage = questionLocaleService.getAnswerErrorMessage();
        assertThat(answerErrMessage).isEqualTo(TEST_ANSWER_ERR_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение об отсутствии корректных вопросов")
    void shouldGetLocalizedNoValidQuestionsMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_NO_VALID_QUESTIONS), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_NO_VALID_QUESTIONS_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_NO_VALID_QUESTIONS), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_NO_VALID_QUESTIONS_MSG);

        setLocale(LOCALE_RU);
        String noValidQuestionsMessage = questionLocaleService.getNoValidQuestionsMessage();
        assertThat(noValidQuestionsMessage).isEqualTo(TEST_NO_VALID_QUESTIONS_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        noValidQuestionsMessage = questionLocaleService.getNoValidQuestionsMessage();
        assertThat(noValidQuestionsMessage).isEqualTo(TEST_NO_VALID_QUESTIONS_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение о пустом перечне вопросов")
    void shouldGetLocalizedEmptyQuestionsMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_EMPTY_MESSAGE_LIST), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_EMPTY_QUESTIONS_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_EMPTY_MESSAGE_LIST), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_EMPTY_QUESTIONS_MSG);

        setLocale(LOCALE_RU);
        String emptyQuestionsMessage = questionLocaleService.getEmptyQuestionsMessage();
        assertThat(emptyQuestionsMessage).isEqualTo(TEST_EMPTY_QUESTIONS_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        emptyQuestionsMessage = questionLocaleService.getEmptyQuestionsMessage();
        assertThat(emptyQuestionsMessage).isEqualTo(TEST_EMPTY_QUESTIONS_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение о вопросе (Вопрос №...)")
    void shouldGetLocalizedQuestionAskMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_QUESION_ASK), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_QUESTIONS_ASK_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_QUESION_ASK), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_QUESTIONS_ASK_MSG);

        setLocale(LOCALE_RU);
        String emptyQuestionsMessage = questionLocaleService.getQuestionAskMessage();
        assertThat(emptyQuestionsMessage).isEqualTo(TEST_QUESTIONS_ASK_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        emptyQuestionsMessage = questionLocaleService.getQuestionAskMessage();
        assertThat(emptyQuestionsMessage).isEqualTo(TEST_QUESTIONS_ASK_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение - информацию о приложении")
    void shouldGetLocalizedAboutMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_ABOUT), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_ABOUT_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_ABOUT), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_ABOUT_MSG);

        setLocale(LOCALE_RU);
        String emptyQuestionsMessage = questionLocaleService.getAboutMessage();
        assertThat(emptyQuestionsMessage).isEqualTo(TEST_ABOUT_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        emptyQuestionsMessage = questionLocaleService.getAboutMessage();
        assertThat(emptyQuestionsMessage).isEqualTo(TEST_ABOUT_MSG);
    }

    @Test
    @DisplayName("Должны получить локализованное сообщение о результатах тестировния")
    void shouldGetLocalizedResultMessage() throws IllegalAccessException {
        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_RESULT), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_RU)))
        ).willReturn(TEST_RESULT_MSG_RU);

        given(messageSource.getMessage(
                eq(QuestionLocaleServiceImpl.STRINGS_RESULT), any(Object[].class), eq(Locale.forLanguageTag(LOCALE_DEFAULT)))
        ).willReturn(TEST_RESULT_MSG);

        setLocale(LOCALE_RU);
        String resultMessage = questionLocaleService.getResultMessage(true, false, 0,0,0);
        assertThat(resultMessage).isEqualTo(TEST_RESULT_MSG_RU);

        setLocale(LOCALE_DEFAULT);
        resultMessage = questionLocaleService.getResultMessage(true, false, 0,0,0);
        assertThat(resultMessage).isEqualTo(TEST_RESULT_MSG);
    }

    private void setLocale(String locale) throws IllegalAccessException {
        FieldUtils.writeField(questionLocaleService, "defaultLocale", locale, true);
    }
}
