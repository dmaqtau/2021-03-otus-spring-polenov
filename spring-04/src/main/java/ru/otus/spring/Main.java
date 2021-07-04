package ru.otus.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.otus.spring.service.QuestionService;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(Main .class, args);

        QuestionService questionService = app.getBean ( QuestionService.class);

        // Начинаем тестирование студента
        try {
            questionService.startStudentExamination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
