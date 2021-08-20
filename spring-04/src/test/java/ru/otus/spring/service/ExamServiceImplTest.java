package ru.otus.spring.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;
import ru.otus.spring.util.Const;
import ru.otus.spring.utils.QuestionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ExamServiceImplTest {
    @MockBean
    private QuestionDao questionDao;
    @MockBean
    private IOService ioService;
    @MockBean
    private LocaleServiceImpl localeService;
    @MockBean
    private QuestionConfig questionConfig;
    @MockBean
    private QuestionValidationService questionValidationService;
    @MockBean
    private AnswerReaderService answerReaderService;
    @Autowired
    private ExamServiceImpl examService;

    @Captor
    private ArgumentCaptor<String> outArgCaptor;

    private static final String WELCOME_MSG = "welcome_msg";
    private static final String PASSED_MSG = "passed_msg";
    private static final String FAILED_MSG = "failed_msg";
    private static final String PASSED_PERFECT_MSG = "passed_perfect_msg";
    private static final String NOT_PASSED_MSG = "not_passed_msg";
    private static final String WRONG_INPUT_MSG = "wrong_input_msg";

    private static final int CORRECT_ANSWER_IDX = 0;
    private static final int INCORRECT_ANSWER_IDX = 1;

    @DisplayName("Должны корректно обработать факт прохождения тестирования")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldProcessPassedResult(boolean isPassed) throws IOException {
        final int questionsCount = 6;
        List<Question> testQuestions = QuestionUtils.getSingleTestQuestion(questionsCount, CORRECT_ANSWER_IDX);

        given(localeService.getLocalizedString(Const.STRINGS_WELCOME)).willReturn(WELCOME_MSG);
        given(questionConfig.getQualifyAnswerQty()).willReturn(1);

        given(localeService.getLocalizedString(
                Const.STRINGS_RESULT,
                "", (isPassed ? "": localeService.getLocalizedString(Const.STRINGS_FAILED)),
                (isPassed ? "1" : "0"), "1", "1")
        ).willReturn(isPassed ? PASSED_MSG : FAILED_MSG);

        given(localeService.getLocalizedString(Const.STRINGS_PERFECT)).willReturn("");

        if(isPassed){
            given(ioService.readLine()).willReturn ("1");       // Правильный ответ
            given(answerReaderService.getUserAnswer(anyInt())).willReturn(new GivenAnswer(CORRECT_ANSWER_IDX, ""));
        } else{
            given(ioService.readLine()).willReturn ("2");       // Неправильный ответ
            given(answerReaderService.getUserAnswer(anyInt())).willReturn(new GivenAnswer(INCORRECT_ANSWER_IDX, ""));
        }

        given(questionDao.getQuestions()).willReturn(testQuestions);
        given(questionValidationService.validateQuestions(testQuestions)).willReturn(Optional.of(testQuestions));       // Все вопросы валидные

        examService.startStudentExamination();

        // Один вывод - шапка (приветственное сообщение), второй - вопрос, третий - результаты тестирования
        assertHasAllOutputs(testQuestions.size());
        verify(ioService, times(testQuestions.size() + 2)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();

        for(String opt: testQuestions.get(0).getAnswers().stream().map(AnswerOption::getText).collect(Collectors.toList())){
            assertTrue(outputs.get(1).contains(opt));
        }

        assertHasWelcomeOutput(outputs);

        if(isPassed){
            assertThat(outputs.get(outputs.size() - 1)).contains(PASSED_MSG);
            assertThat(outputs.get(outputs.size() - 1)).doesNotContain(FAILED_MSG);
        } else {
            assertThat(outputs.get(outputs.size() - 1)).doesNotContain(PASSED_MSG);
            assertThat(outputs.get(outputs.size() - 1)).contains(FAILED_MSG);
        }
    }

    @DisplayName("Должны корректно обработать ответы с отличием")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldProcessPerfectAnswers(boolean isPerfect) throws IOException {
        final int questionsCount = 5;
        List<Question> testQuestions = QuestionUtils.getSingleTestQuestion(questionsCount, CORRECT_ANSWER_IDX);

        given(localeService.getLocalizedString(Const.STRINGS_WELCOME)).willReturn(WELCOME_MSG);

        given(localeService.getLocalizedString(
                Const.STRINGS_RESULT,
                (isPerfect ? PASSED_PERFECT_MSG : ""), "",
                (isPerfect ? "1": "0"), "0", "1")
        ).willReturn(isPerfect ? PASSED_PERFECT_MSG : PASSED_MSG);

        if(isPerfect){
            given(ioService.readLine()).willReturn ("1");       // Правильный ответ
            given(localeService.getLocalizedString(Const.STRINGS_PERFECT)).willReturn(PASSED_PERFECT_MSG);
            given(answerReaderService.getUserAnswer(anyInt())).willReturn(new GivenAnswer(CORRECT_ANSWER_IDX, ""));
        } else {
            given(ioService.readLine()).willReturn ("2");       // Неправильный ответ
            given(answerReaderService.getUserAnswer(anyInt())).willReturn(new GivenAnswer(INCORRECT_ANSWER_IDX, ""));
        }

        given(questionDao.getQuestions()).willReturn(testQuestions);
        given(questionValidationService.validateQuestions(testQuestions)).willReturn(Optional.of(testQuestions));       // Все вопросы валидные


        examService.startStudentExamination();

        // Один вывод - шапка (приветственное сообщение), второй - вопрос, третий - результаты тестирования
        assertHasAllOutputs(testQuestions.size());
        verify(ioService, times(testQuestions.size() + 2)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();

        for(String opt: testQuestions.get(0).getAnswers().stream().map(AnswerOption::getText).collect(Collectors.toList())){
            assertTrue(outputs.get(1).contains(opt));
        }

        assertHasWelcomeOutput(outputs);

        if(isPerfect){
            assertThat(outputs.get(outputs.size() - 1)).contains(PASSED_PERFECT_MSG);
            assertThat(outputs.get(outputs.size() - 1)).doesNotContain(PASSED_MSG);
        } else {
            assertThat(outputs.get(outputs.size() - 1)).doesNotContain(PASSED_PERFECT_MSG);
            assertThat(outputs.get(outputs.size() - 1)).contains(PASSED_MSG);
        }
    }

    @DisplayName("Должно быть прервано выполнение тестирования студента в случае отсутствия вопросов")
    @Test
    void shouldInterruptExamOnNoQuestions() {
        given(questionDao.getQuestions()).willReturn(null);
        given(questionValidationService.validateQuestions(null)).willReturn(Optional.empty());
        examService.startStudentExamination();

        assertAll(() -> verify(ioService, never()).out(outArgCaptor.capture()));
    }

    @DisplayName("Должны успешно пройти тест при должном количестве правильных ответов")
    @Test
    void shouldPassExam() throws IOException {
        given(localeService.getLocalizedString(Const.STRINGS_RESULT, "", "", "2", "2", "3")).willReturn(PASSED_MSG);

        testPassExam(ExpectedPassResult.PASS);
    }

    @DisplayName("Должны успешно пройти тест с отличием, если все ответы правильные")
    @Test
    void shouldPerfectlyPassExam() throws IOException {
        given(localeService.getLocalizedString(Const.STRINGS_RESULT, PASSED_PERFECT_MSG, "", "3", "2", "3")).willReturn(PASSED_PERFECT_MSG);

        given(localeService.getLocalizedString(Const.STRINGS_PERFECT)).willReturn(PASSED_PERFECT_MSG);

        testPassExam(ExpectedPassResult.PASS_PERFECT);
    }

    @DisplayName("Должны провалить тест, если правильных ответов менее, чем необходимое кол-во")
    @Test
    void shouldFailExam() throws IOException {
        given(localeService.getLocalizedString(Const.STRINGS_RESULT, "", FAILED_MSG, "0", "2", "3")).willReturn(NOT_PASSED_MSG);
        given(localeService.getLocalizedString(Const.STRINGS_FAILED)).willReturn(FAILED_MSG);

        testPassExam(ExpectedPassResult.FAIL);
    }

    @DisplayName("Должны повторно запросить ввод при неправльном вводе опции ответа")
    @Test
    void shouldRepeatUserInputOnIncorrectAnswerOption() throws IOException {
        List<Question> questions = List.of(
                createQuestion(1, 1),
                createQuestion(2, 0),
                createQuestion(3, 2)
        );

        given(questionDao.getQuestions()).willReturn(questions);
        given(questionValidationService.validateQuestions(questions)).willReturn(Optional.of(questions));       // Все вопросы валидные

        given(localeService.getLocalizedString(eq(Const.STRINGS_RESULT), ArgumentMatchers.<String>any())).willReturn(PASSED_MSG);

        given(answerReaderService.getUserAnswer(anyInt()))
                .willReturn(new GivenAnswer(Const.ERR_GIVEN_ANSWER_IDX, WRONG_INPUT_MSG))
                .willReturn(new GivenAnswer(1, ""))
                .willReturn(new GivenAnswer(0, ""))
                .willReturn(new GivenAnswer(2, ""));

        examService.startStudentExamination();

        verify(answerReaderService, times(4)).getUserAnswer(anyInt());  // Три варианта ответа + 1 повтор из-за неверного ввода
    }

    private Question createQuestion(int ordinal, int correctIdx){
        return Question.builder()
                .correctAnswerIndex(correctIdx)
                .ordinalNumber(ordinal)
                .questionText("test_question_text_" + ordinal)
                .answers(List.of(
                        new AnswerOption(0, "test_answer_option_1"),
                        new AnswerOption(1, "test_answer_option_2"),
                        new AnswerOption(2, "test_answer_option_3"),
                        new AnswerOption(3, "test_answer_option_4")
                )).build();
    }

    private void testPassExam(ExpectedPassResult expectedPassResult) throws IOException {
        List<Question> questions = List.of(
                createQuestion(1, 1),
                createQuestion(2, 0),
                createQuestion(3, 2)
        );
        given(questionDao.getQuestions()).willReturn(questions);
        given(questionConfig.getQualifyAnswerQty()).willReturn(questions.size() - 1);
        given(questionValidationService.validateQuestions(questions)).willReturn(Optional.of(questions));       // Все вопросы валидные

        given(answerReaderService.getUserAnswer(anyInt()))
                .willReturn(new GivenAnswer(ExpectedPassResult.FAIL.equals(expectedPassResult) ? 0: 1, ""))
                .willReturn(new GivenAnswer(ExpectedPassResult.PASS_PERFECT.equals(expectedPassResult) ||
                        ExpectedPassResult.PASS.equals(expectedPassResult) ? 0: 1, ""))
                .willReturn(new GivenAnswer(ExpectedPassResult.PASS_PERFECT.equals(expectedPassResult) ? 2: 0, ""));

        examService.startStudentExamination();

        // Шапка-приветствие + questions.size() вопросов + результат тестирования
        assertHasAllOutputs(questions.size());
        verify(ioService, times(questions.size() + 2)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(outputs.size() - 1)).contains(expectedPassResult.getExpectedOutput());
    }

    @Getter
    private enum ExpectedPassResult{
        PASS(PASSED_MSG),
        PASS_PERFECT(PASSED_PERFECT_MSG),
        FAIL(NOT_PASSED_MSG);

        private String expectedOutput;

        ExpectedPassResult(String expectedOutput){
            this.expectedOutput = expectedOutput;
        }
    }


    // Убедимся, что в консоль был вывод:
    // приветственного сообщения, вопросов, результатов тестирования.
    private void assertHasAllOutputs(int questionsCount){
        assertAll(
                () -> verify(localeService).getLocalizedString(Const.STRINGS_WELCOME),
                () -> verify(localeService, times(questionsCount)).getLocalizedString(Const.STRINGS_QUESION_ASK),
                () -> verify(localeService).getLocalizedString(eq(Const.STRINGS_RESULT), ArgumentMatchers.<String>any())
        );
    }

    private static void assertHasWelcomeOutput(List<String> outputs){
        assertThat(outputs.get(0)).contains(WELCOME_MSG);
    }
}
