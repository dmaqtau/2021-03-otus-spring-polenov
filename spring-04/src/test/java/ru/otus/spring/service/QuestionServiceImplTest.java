package ru.otus.spring.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;
import ru.otus.spring.util.Const;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
class QuestionServiceImplTest {
    @Mock
    private QuestionDao questionDao;
    @Mock
    private IOService ioService;
    @Mock
    private QuestionLocaleServiceImpl questionLocaleService;
    @Mock
    private QuestionConfig questionConfig;
    @InjectMocks
    private QuestionServiceImpl questionService;

    @Captor
    private ArgumentCaptor<String> outArgCaptor;

    private static final String WELCOME_MSG = "welcome_msg";
    private static final String PASSED_MSG = "passed_msg";
    private static final String PASSED_PERFECT_MSG = "passed_perfect_msg";
    private static final String EMPTY_QUESTIONS_LIST_MSG = "empty_questions_list_msg";
    private static final String CORRUPT_QUESTIONS_LIST_MSG = "corrupt_questions_list_msg";
    private static final String NOT_PASSED_MSG = "not_passed_msg";
    private static final String WRONG_INPUT_MSG = "wrong_input_msg";
    private static final String WRONG_ANSWER_OPTION_MSG = "wrong_answer_option_msg";

    @DisplayName("Все вопросы и ответы должны отобразиться в консоли")
    @Test
    void shouldHaveQuestionsAndAnswersInOutput() throws IOException {
        List<Question> testQuestions = new ArrayList<>();
        List<AnswerOption> answerOptions = new ArrayList<>();

        when(questionLocaleService.getWelcomeMessage()).thenReturn(WELCOME_MSG);
        when(questionLocaleService.getResultMessage(eq(true), eq(true), anyInt(), anyInt(), anyInt())).thenReturn(PASSED_PERFECT_MSG);

        answerOptions.add(new AnswerOption(1, "test_answer_1"));
        answerOptions.add(new AnswerOption(2, "test_answer_2"));
        answerOptions.add(new AnswerOption(3, "test_answer_3"));
        answerOptions.add(new AnswerOption(4, "test_answer_4"));
        answerOptions.add(new AnswerOption(5, "test_answer_5"));
        testQuestions.add(new Question(1, "Test question?", answerOptions, 0));

        given(ioService.readLine()).willReturn ("1");
        given(questionDao.getQuestions()).willReturn(testQuestions);
        questionService.startStudentExamination();

        // Один вывод - шапка (приветственное сообщение), второй - вопрос, третий - результаты тестирования
        assertHasAllOutputs(testQuestions.size());
        verify(ioService, times(testQuestions.size() + 2)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();

        for(String opt: answerOptions.stream().map(AnswerOption::getText).collect(Collectors.toList())){
            assertTrue(outputs.get(1).contains(opt));
        }

        assertHasWelcomeOutput(outputs);
        assertThat(outputs.get(outputs.size() - 1)).contains(PASSED_PERFECT_MSG);
    }

    @DisplayName("Должно быть прервано выполнение тестирования студента в случае отсутствия вопросов")
    @Test
    void shouldInterruptExamOnNoQuestions() {
        when(questionLocaleService.getEmptyQuestionsMessage()).thenReturn(EMPTY_QUESTIONS_LIST_MSG);

        given(questionDao.getQuestions()).willReturn(null);
        questionService.startStudentExamination();

        verify(ioService, times(1)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(0)).contains(EMPTY_QUESTIONS_LIST_MSG);
    }

    @DisplayName("Должно быть прервано выполнение тестирования студента в случае ошибки валидации вопросов")
    @Test
    void shouldInterruptExamOnCorruptQuestions() {
        when(questionLocaleService.getNoValidQuestionsMessage()).thenReturn(CORRUPT_QUESTIONS_LIST_MSG);

        given(questionDao.getQuestions()).willReturn(List.of(new Question()));      // Пустой ответ, который не проходит валидацию
        questionService.startStudentExamination();

        verify(ioService, times(1)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs).isNotNull().isNotEmpty();
        assertThat(outputs.get(0)).isNotNull().contains(CORRUPT_QUESTIONS_LIST_MSG);
    }

    @DisplayName("Должны успешно пройти тест при должном количестве правильных ответов")
    @Test
    void shouldPassExam() throws IOException {
        when(questionLocaleService.getResultMessage(eq(true), eq(false), anyInt(), anyInt(), anyInt())).thenReturn(PASSED_MSG);
        testPassExam(ExpectedPassResult.PASS);
    }

    @DisplayName("Должны успешно пройти тест с отличием, если все ответы правильные")
    @Test
    void shouldPerfectlyPassExam() throws IOException {
        when(questionLocaleService.getResultMessage(eq(true), eq(true), anyInt(), anyInt(), anyInt())).thenReturn(PASSED_PERFECT_MSG);
        testPassExam(ExpectedPassResult.PASS_PERFECT);
    }

    @DisplayName("Должны провалить тест, если правильных ответов менее, чем необходимое кол-во")
    @Test
    void shouldFailExam() throws IOException {
        when(questionLocaleService.getResultMessage(eq(false), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(NOT_PASSED_MSG);
        testPassExam(ExpectedPassResult.FAIL);
    }

    @DisplayName("Должно выдаваться сообщение о некорректном выборе опции ответа")
    @Test
    void shouldWarnAboutWrongChosenAnswerOption() throws IOException {
        List<Question> questions = List.of(
                createQuestion(1, 1),
                createQuestion(2, 0),
                createQuestion(3, 2)
        );

        given(questionDao.getQuestions()).willReturn(questions);
        when(questionLocaleService.getWrongAnswerInputMessage()).thenReturn(WRONG_INPUT_MSG);
        assertSingleWrongGivenAnswer(questions, "100", WRONG_INPUT_MSG);
        assertSingleWrongGivenAnswer(questions, "abc", WRONG_INPUT_MSG);


        Mockito.reset(questionLocaleService);
        when(questionLocaleService.getWrongAnswerOptionMessage(anyInt())).thenReturn(WRONG_ANSWER_OPTION_MSG);
        assertSingleWrongGivenAnswer(questions, "z", WRONG_ANSWER_OPTION_MSG);
        assertSingleWrongGivenAnswer(questions, "8", WRONG_ANSWER_OPTION_MSG);
    }

    private void assertSingleWrongGivenAnswer(List<Question> questions, String givenAnswer, String expectedMessage) throws IOException {
        Mockito.reset(ioService);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(ioService.readLine())
                .thenReturn(givenAnswer)      // Заведомо неверный индекс опции ответа
                .thenReturn("1");             // Далее отвечаем правильно, чтобы не уйти в бесконечный цикл

        questionService.startStudentExamination();

        // Приветствие + кол-во вопросов + один вопрос задаётся два раза из-за неверного ввода + результат тестирования
        verify(ioService, times(questions.size() + 3)).out(captor.capture());        // Заголовок о начале тестирования + кол-во вопросов + один был задан повторно

        List<String> outputs = captor.getAllValues();
        assertThat(outputs.get(3).contains(expectedMessage));
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

        when(ioService.readLine())
                .thenReturn(ExpectedPassResult.FAIL.equals(expectedPassResult) ? "1": "2")
                .thenReturn(ExpectedPassResult.PASS_PERFECT.equals(expectedPassResult) ||
                        ExpectedPassResult.PASS.equals(expectedPassResult) ? "1" : "2")
                .thenReturn(ExpectedPassResult.PASS_PERFECT.equals(expectedPassResult) ? "3" : "1");

        questionService.startStudentExamination();

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
                () -> verify(questionLocaleService).getWelcomeMessage(),
                () -> verify(questionLocaleService, times(questionsCount)).getQuestionAskMessage(),
                () -> verify(questionLocaleService).getResultMessage(anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyInt())
        );
    }

    private static void assertHasWelcomeOutput(List<String> outputs){
        assertThat(outputs.get(0)).contains(WELCOME_MSG);
    }
}
