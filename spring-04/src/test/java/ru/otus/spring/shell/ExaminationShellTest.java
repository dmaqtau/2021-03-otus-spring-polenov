package ru.otus.spring.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import ru.otus.spring.service.QuestionService;

import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
class ExaminationShellTest {
    @Mock
    private QuestionService questionService;
    @InjectMocks
    private ExaminationShell examinationShell;

    @Test
    @DisplayName("Должно начаться тестирование студентов")
    void shouldCallExaminationStart(){
        examinationShell.startStudentExamination();
        verify(questionService).startStudentExamination();
    }

    @Test
    @DisplayName("Должна быть выведена информация о приложении")
    void shouldAbout(){
        examinationShell.about();
        verify(questionService).about();
    }
}
