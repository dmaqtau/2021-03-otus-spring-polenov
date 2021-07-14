package ru.otus.spring.config;

import java.io.InputStream;
import java.io.PrintStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IOConfig {
    @Bean
    public PrintStream consoleOutputStream(){
        return System.out;
    }

    @Bean
    public InputStream consoleInputStream(){
        return System.in;
    }
}
