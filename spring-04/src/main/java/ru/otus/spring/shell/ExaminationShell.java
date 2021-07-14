package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.service.AboutAppService;
import ru.otus.spring.service.ExamService;

@ShellComponent
@RequiredArgsConstructor
public class ExaminationShell {
    private final ExamService examService;
    private final AboutAppService aboutAppService;

    @ShellMethod(key = "about", value = "About current application")
    String about(){
        return aboutAppService.getAboutAppMessage();
    }

    @ShellMethod(key = "start", value = "Start the examination")
    void startStudentExamination(){
        examService.startStudentExamination();
    }
}
