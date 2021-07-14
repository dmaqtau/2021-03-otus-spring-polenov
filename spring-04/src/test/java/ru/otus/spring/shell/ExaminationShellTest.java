package ru.otus.spring.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.service.AboutAppService;
import ru.otus.spring.service.ExamService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ExaminationShellTest {
    @MockBean
    private ExamService examService;
    @MockBean
    private AboutAppService aboutAppService;
    @Autowired
    private ExaminationShell examinationShell;

    private static final String ABOUT_APP_MSG = "test_about";

    @Test
    @DisplayName("Должно начаться тестирование студентов")
    void shouldCallExaminationStart(){
        examinationShell.startStudentExamination();
        verify(examService).startStudentExamination();
    }

    @Test
    @DisplayName("Должна быть выведена информация о приложении")
    void shouldAbout(){
        given(aboutAppService.getAboutAppMessage()).willReturn(ABOUT_APP_MSG);

        assertAll(
                () -> assertThat(examinationShell.about()).isEqualTo(ABOUT_APP_MSG),
                () -> verify(aboutAppService).getAboutAppMessage()
        );
    }
}
