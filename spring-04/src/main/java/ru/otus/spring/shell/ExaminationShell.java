package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.service.QuestionService;

@ShellComponent
@RequiredArgsConstructor
public class ExaminationShell {
    private final QuestionService questionService;

    @ShellMethod(key = "about", value = "About current application")
    public String about(){
        return questionService.about();
    }

    @ShellMethod(key = "start", value = "Start the examination")
    public void startStudentExamination(){
        questionService.startStudentExamination();
    }
}
