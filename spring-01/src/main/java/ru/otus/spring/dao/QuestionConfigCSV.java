package ru.otus.spring.dao;

import org.springframework.stereotype.Component;

@Component
public class QuestionConfigCSV implements QuestionConfig {
    private static final int QUESTION_TEXT_COLUMN_IDX = 0;
    private static final int CORRECT_ANSWER_COLUMN_IDX = 1;
    private static final int FIRST_QUESTION_COLUMN_IDX = 2;
    private static final int MAX_QUESTION_COUNT = 10;

    public int getQuestionColumnIdx() {
        return QUESTION_TEXT_COLUMN_IDX;
    }

    public int getCorrectAnswerColumnIdx() {
        return CORRECT_ANSWER_COLUMN_IDX;
    }

    public int getMaxQuestionCount() {
        return MAX_QUESTION_COUNT;
    }

    public int getFirstQuestionColumnIdx() {
        return FIRST_QUESTION_COLUMN_IDX;
    }

    public char getDelimiter() {
        return ';';
    }
}
