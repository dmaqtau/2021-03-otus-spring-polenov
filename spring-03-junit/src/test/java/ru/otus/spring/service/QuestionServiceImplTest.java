package ru.otus.spring.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {
    @Mock
    private QuestionDao questionDao;
    @Mock
    private IOService ioService;
    @Mock
    private QuestionConfig questionConfig;
    @InjectMocks
    private QuestionServiceImpl questionService;

    @Captor
    private ArgumentCaptor<String> outArgCaptor;

    @DisplayName("Все вопросы и ответы должны отобразиться в консоли")
    @Test
    void shouldHaveQuestionsAndAnswersInOutput() throws IOException {
        List<Question> testQuestions = new ArrayList<>();
        List<AnswerOption> answerOptions = new ArrayList<>();

        answerOptions.add(new AnswerOption(1, "test_answer_1"));
        answerOptions.add(new AnswerOption(2, "test_answer_2"));
        answerOptions.add(new AnswerOption(3, "test_answer_3"));
        answerOptions.add(new AnswerOption(4, "test_answer_4"));
        answerOptions.add(new AnswerOption(5, "test_answer_5"));
        testQuestions.add(new Question(1, "Test question?", answerOptions, 0));

        given(ioService.readLine()).willReturn ("1");
        given(questionDao.getQuestions()).willReturn(testQuestions);
        questionService.startStudentExamination();

        // Один вывод - вопрос, второй - результаты тестирования
        verify(ioService, times(2)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();

        for(String opt: answerOptions.stream().map(AnswerOption::getText).collect(Collectors.toList())){
            assertTrue(outputs.get(0).contains(opt));
        }
    }

    @DisplayName("Должно быть прервано выполнение тестирования студента в случае отсутствия вопросов")
    @Test
    void shouldInterruptExamOnNoQuestions() {
        given(questionDao.getQuestions()).willReturn(null);
        questionService.startStudentExamination();

        verify(ioService, times(1)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(0)).contains("questions list has not been prepared");
    }

    @DisplayName("Должно быть прервано выполнение тестирования студента в случае ошибки валидации вопросов")
    @Test
    void shouldInterruptExamOnCorruptQuestions() {
        given(questionDao.getQuestions()).willReturn(List.of(new Question()));      // Пустой ответ, который не проходит валидацию
        questionService.startStudentExamination();

        verify(ioService, times(1)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(0)).contains("questions are corrupt");
    }

    @DisplayName("Должны успешно пройти тест при должном количестве правильных ответов")
    @Test
    void shouldPassExam() throws IOException {
        testPassExam(ExpectedPassResult.PASS);
    }

    @DisplayName("Должны успешно пройти тест с отличием, если все ответы правильные")
    @Test
    void shouldPerfectlyPassExam() throws IOException {
        testPassExam(ExpectedPassResult.PASS_PERFECT);
    }

    @DisplayName("Должны провалить тест, если правильных ответов менее, чем необходимое кол-во")
    @Test
    void shouldFailExam() throws IOException {
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
        assertSingleWrongGivenAnswer(questions, "100", "Wrong input");
        assertSingleWrongGivenAnswer(questions, "abc", "Wrong input");
        assertSingleWrongGivenAnswer(questions, "z", "Wrong answer option");
        assertSingleWrongGivenAnswer(questions, "8", "Wrong answer option");
    }

    private void assertSingleWrongGivenAnswer(List<Question> questions, String givenAnswer, String expectedMessage) throws IOException {
        Mockito.reset(ioService);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(ioService.readLine())
                .thenReturn(givenAnswer)      // Заведомо неверный индекс опции ответа
                .thenReturn("1");             // Далее отвечаем правильно, чтобы не уйти в бесконечный цикл

        questionService.startStudentExamination();
        verify(ioService, times(questions.size() + 2)).out(captor.capture());        // Заголовок о начале тестирования + кол-во вопросов + один был задан повторно

        List<String> outputs = captor.getAllValues();
        assertThat(outputs.get(1)).contains(expectedMessage);
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
        given(questionConfig.getPassAnswerQty()).willReturn(questions.size() - 1);

        when(ioService.readLine())
                .thenReturn(ExpectedPassResult.FAIL.equals(expectedPassResult) ? "1": "2")
                .thenReturn(ExpectedPassResult.PASS_PERFECT.equals(expectedPassResult) ||
                        ExpectedPassResult.PASS.equals(expectedPassResult) ? "1" : "2")
                .thenReturn(ExpectedPassResult.PASS_PERFECT.equals(expectedPassResult) ? "3" : "1");

        questionService.startStudentExamination();

        // Шапка-приветствие + questions.size() вопросов +
        verify(ioService, times(questions.size() + 1)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();
        assertThat(outputs.get(questions.size())).contains(expectedPassResult.getExpectedOutput());
    }

    @Getter
    private enum ExpectedPassResult{
        PASS("You have passed the exam"),
        PASS_PERFECT("You have perfectly passed the exam"),
        FAIL("You have NOT passed the exam");

        private String expectedOutput;

        ExpectedPassResult(String expectedOutput){
            this.expectedOutput = expectedOutput;
        }
    }
}
