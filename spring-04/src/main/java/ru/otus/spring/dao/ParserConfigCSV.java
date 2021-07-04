package ru.otus.spring.dao;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ParserConfigCSV implements ParserConfig {
    private int questionColumnIdx;
    private int correctAnswerColumnIdx;
    private int firstQuestionColumnIdx;
    private int questionAskQty;
    private char delimiter;
    private String questionFileTemplate;
    private String locale;

    private static final String FILE_EXTENSION = "csv";

    public ParserConfigCSV(@Value("${questionfile.template}") String questionFileTemplate,
                           @Value("${question.column.index}") int questionColumnIdx,
                           @Value("${answer.column.index}") int correctAnswerColumnIdx,
                           @Value("${question.ask.qty}") int questionAskQty,
                           @Value("${question.first.column.index}") int firstQuestionColumnIdx,
                           @Value("${csv.delimiter}") char delimiter,
                           @Value("${locale.default}") String defaultLocale){
        this.questionFileTemplate = questionFileTemplate;
        this.questionColumnIdx = questionColumnIdx;
        this.correctAnswerColumnIdx = correctAnswerColumnIdx;
        this.questionAskQty = questionAskQty;
        this.firstQuestionColumnIdx = firstQuestionColumnIdx;
        this.delimiter = delimiter;
        this.locale = defaultLocale;
    }

    @Override
    public String getQuestionFilePath() {
        if(StringUtils.isBlank(locale)){
            return questionFileTemplate + "." + FILE_EXTENSION;
        }
        return questionFileTemplate + "_" + locale.replace("-", "_") + "." + FILE_EXTENSION;
    }
}
