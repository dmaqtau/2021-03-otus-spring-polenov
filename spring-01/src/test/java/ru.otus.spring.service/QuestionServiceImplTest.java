package ru.otus.spring.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.otus.spring.dao.QuestionDaoCSV;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceImplTest {
    @Mock
    private QuestionDaoCSV questionDaoCSV;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testStudent() {
        List<Question> testQuestions = new ArrayList<>();
        List<AnswerOption> answerOptions = new ArrayList<>();

        answerOptions.add(new AnswerOption(1, "test_answer_1"));
        answerOptions.add(new AnswerOption(2, "test_answer_2"));
        answerOptions.add(new AnswerOption(3, "test_answer_3"));
        answerOptions.add(new AnswerOption(4, "test_answer_4"));
        answerOptions.add(new AnswerOption(5, "test_answer_5"));
        testQuestions.add(new Question(1, "Test question?", answerOptions, 0));

        when(questionDaoCSV.getQuestions()).thenReturn(testQuestions);
        QuestionServiceImpl questionService = new QuestionServiceImpl(questionDaoCSV);
        questionService.testStudent();

        String output = outputStreamCaptor.toString();
        assertNotNull(output);

        answerOptions.forEach(op -> assertTrue(output.contains(op.getText())));
    }
}
