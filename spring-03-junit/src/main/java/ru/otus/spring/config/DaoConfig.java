package ru.otus.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.spring.dao.ParserConfig;
import ru.otus.spring.dao.ParserConfigCSV;

@Configuration
public class DaoConfig {
    @Bean
    public ParserConfig parseConfig() {
        return new ParserConfigCSV();
    }
}
