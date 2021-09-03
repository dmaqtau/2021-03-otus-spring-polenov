package ru.otus.spring.utils;

import java.util.ArrayList;
import java.util.List;

import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

public class QuestionUtils {
    public static List<Question> getSingleTestQuestion(int answerOptionsCount, int correctAnswerIndex){
        List<Question> testQuestions = new ArrayList<>();
        List<AnswerOption> answerOptions = new ArrayList<>();

        for(int i = 0; i < answerOptionsCount; i++){
            answerOptions.add(new AnswerOption(i, "test_answer_" + i));
        }
        testQuestions.add(new Question(1, "Test question?", answerOptions, correctAnswerIndex));
        return testQuestions;
    }
}
