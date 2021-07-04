package ru.otus.spring.dao;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exception.CSVParseException;
import ru.otus.spring.util.Const;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@ActiveProfiles(Const.TEST_PROFILE)
class QuestionDaoCSVTest {

    @Autowired
    ParserConfigCSV parserConfig;

    private static final int FIXED_ANSWER_OPTIONS_COUNT = 4;

    @DisplayName("Должен распарсить корректный CSV-файл с вопросами")
    @Test
    void shouldParseCorrectCsvFile(){
        QuestionDaoCSV questionDaoCSV = new QuestionDaoCSV(parserConfig);
        List<Question> questionList = questionDaoCSV.getQuestions();

        assertThat(questionList).isNotNull().hasSize(parserConfig.getQuestionAskQty());
        questionList.forEach(q -> {
            assertAll(
                    () -> assertThat(q).isNotNull(),
                    () -> assertThat(q.getOrdinalNumber()).isPositive(),
                    () -> assertThat(q.getQuestionText()).isNotBlank(),
                    () -> assertThat(q.getAnswers()).isNotNull().hasSize(FIXED_ANSWER_OPTIONS_COUNT)
            );
        });
    }

    @DisplayName("Должна быть выброшена ошибка, если CSV-файл с вопросами не найден")
    @Test
    void shoundThrowExceptionOnEmptyFilePath(){
        String fileTemplate = parserConfig.getQuestionFileTemplate();
        parserConfig.setQuestionFileTemplate("error_path");

        QuestionDaoCSV questionDaoCSV = new QuestionDaoCSV(parserConfig);
        assertThrows(CSVParseException.class, questionDaoCSV::getQuestions);
        parserConfig.setQuestionFileTemplate(fileTemplate);
    }

    @DisplayName("Должна быть выброшена ошибка, если путь к CSV-файлу не задан")
    @Test
    void shoundThrowExceptionOnNotFoundCSVFile(){
        String fileTemplate = parserConfig.getQuestionFileTemplate();
        parserConfig.setQuestionFileTemplate("");

        QuestionDaoCSV questionDaoCSV = new QuestionDaoCSV(parserConfig);
        assertThrows(CSVParseException.class, questionDaoCSV::getQuestions);

        parserConfig.setQuestionFileTemplate(null);
        assertThrows(CSVParseException.class, questionDaoCSV::getQuestions);

        parserConfig.setQuestionFileTemplate(fileTemplate);
    }

}
