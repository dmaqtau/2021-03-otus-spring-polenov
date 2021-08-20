package ru.otus.spring.dao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseConfigCSV implements ParseConfig {
    private int questionColumnIdx;
    private int correctAnswerColumnIdx;
    private int firstQuestionColumnIdx;
    private int questionAskQty;
    private char delimiter;
    private String questionFilePath;
}
