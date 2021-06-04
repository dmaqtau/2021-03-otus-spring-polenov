package ru.otus.spring.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@PropertySource("classpath:config.properties")
public class ParserConfigCSV implements ParserConfig {
    @Value("${questionfile.path}")
    private String questionFilePath;

    @Value("${question.column.index}")
    private int questionColumnIdx;

    @Value("${answer.column.index}")
    private int correctAnswerColumnIdx;

    @Value("${question.ask.qty}")
    private int questionAskQty;

    @Value("${firstquestion.column.index}")
    private int firstQuestionColumnIdx;

    @Value("${csv.delimiter}")
    private char csvDelimiter;

    @Override
    public char getDelimiter() {
        return csvDelimiter;
    }
}
