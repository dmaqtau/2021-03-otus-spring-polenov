package ru.otus.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionConfigImpl;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.service.IOService;
import ru.otus.spring.service.IOServiceImpl;
import ru.otus.spring.service.QuestionService;
import ru.otus.spring.service.QuestionServiceImpl;

@Configuration
@PropertySource("classpath:config.properties")
public class ServiceConfig {

    @Value("${qualify.answer.qty}")
    private int qualifyAnswerQty;

    @Bean
    public QuestionConfig questionConfig() {
        QuestionConfigImpl questionConfig = new QuestionConfigImpl();
        questionConfig.setPassAnswerQty(qualifyAnswerQty);
        return questionConfig;
    }

    @Bean
    public IOService ioService() {
        return new IOServiceImpl();
    }

    @Bean
    public QuestionService questionService(QuestionDao questionDao, QuestionConfig questionConfig, IOService ioService) {
        return new QuestionServiceImpl(questionDao, questionConfig, ioService);
    }
}
