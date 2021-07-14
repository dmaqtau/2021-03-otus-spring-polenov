package ru.otus.spring.dao;

public interface QuestionConfig {
    int getQuestionColumnIdx();

    int getCorrectAnswerColumnIdx();

    int getMaxQuestionCount();

    int getFirstQuestionColumnIdx();

    char getDelimiter();
}
