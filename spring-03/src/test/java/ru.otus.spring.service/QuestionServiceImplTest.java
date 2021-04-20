package ru.otus.spring.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDaoCSV;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {
    @Mock
    private QuestionDaoCSV questionDaoCSV;
    @Mock
    private IOService ioService;
    @Mock
    private QuestionConfig questionConfig;
    @InjectMocks
    private QuestionServiceImpl questionService;

    @Captor
    private ArgumentCaptor<String> outArgCaptor;

    @BeforeEach
    void setUp() throws IOException {
        given(ioService.readLine()).willReturn ("1");
    }

    @Test
    void testStudent() {
        List<Question> testQuestions = new ArrayList<>();
        List<AnswerOption> answerOptions = new ArrayList<>();

        answerOptions.add(new AnswerOption(1, "test_answer_1"));
        answerOptions.add(new AnswerOption(2, "test_answer_2"));
        answerOptions.add(new AnswerOption(3, "test_answer_3"));
        answerOptions.add(new AnswerOption(4, "test_answer_4"));
        answerOptions.add(new AnswerOption(5, "test_answer_5"));
        testQuestions.add(new Question(1, "Test question?", answerOptions, 0));

        given(questionDaoCSV.getQuestions()).willReturn(testQuestions);
        questionService.startTest();

        // Один вывод - вопрос, второй - результаты тестирования
        verify(ioService, times(2)).out(outArgCaptor.capture());

        List<String> outputs = outArgCaptor.getAllValues();

        for(String opt: answerOptions.stream().map(AnswerOption::getText).collect(Collectors.toList())){
            assertTrue(outputs.get(0).contains(opt));
        }
    }
}
