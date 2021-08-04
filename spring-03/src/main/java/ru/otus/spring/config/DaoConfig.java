package ru.otus.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.spring.dao.ParseConfig;
import ru.otus.spring.dao.ParseConfigCSV;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.dao.QuestionDaoCSV;

@Configuration
@PropertySource("classpath:config.properties")
public class DaoConfig {
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

    @Bean
    public ParseConfig parseConfig() {
        ParseConfigCSV config = new ParseConfigCSV();
        config.setCorrectAnswerColumnIdx(correctAnswerColumnIdx);
        config.setFirstQuestionColumnIdx(firstQuestionColumnIdx);
        config.setQuestionColumnIdx(questionColumnIdx);
        config.setQuestionFilePath(questionFilePath);
        config.setQuestionAskQty(questionAskQty);
        config.setDelimiter(csvDelimiter);
        return config;
    }

    @Bean
    public QuestionDao questionDao() {
        return new QuestionDaoCSV(parseConfig());
    }
}
