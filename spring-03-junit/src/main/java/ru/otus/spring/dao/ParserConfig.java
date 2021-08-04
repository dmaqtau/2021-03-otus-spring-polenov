package ru.otus.spring.dao;

public interface ParserConfig {
    int getQuestionColumnIdx();

    int getCorrectAnswerColumnIdx();

    int getFirstQuestionColumnIdx();

    int getQuestionAskQty();

    String getQuestionFilePath();

    char getDelimiter();
}
