package ru.otus.spring.dao;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exception.CSVParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuestionDaoCSVTest {

    @Mock
    private ParserConfigCSV parserConfig;

    private static final int FIXED_ANSWER_OPTIONS_COUNT = 4;

    private void initFull(){
        given(parserConfig.getDelimiter()).willReturn('[');
        given(parserConfig.getQuestionFilePath()).willReturn("test_questions_junit.csv");
        given(parserConfig.getQuestionAskQty()).willReturn(5);
        given(parserConfig.getQuestionColumnIdx()).willReturn(0);
        given(parserConfig.getCorrectAnswerColumnIdx()).willReturn(1);
        given(parserConfig.getFirstQuestionColumnIdx()).willReturn(2);
    }

    @DisplayName("Должен распарсить корректный CSV-файл с вопросами")
    @Test
    void shouldParseCorrectCsvFile(){
        initFull();
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
        given(parserConfig.getQuestionFilePath()).willReturn("error_file");
        QuestionDaoCSV questionDaoCSV = new QuestionDaoCSV(parserConfig);
        assertThrows(CSVParseException.class, questionDaoCSV::getQuestions);
    }

    @DisplayName("Должна быть выброшена ошибка, если путь к CSV-файлу не задан")
    @Test
    void shoundThrowExceptionOnNotFoundCSVFile(){
        given(parserConfig.getQuestionFilePath()).willReturn("");

        QuestionDaoCSV questionDaoCSV = new QuestionDaoCSV(parserConfig);
        assertThrows(CSVParseException.class, questionDaoCSV::getQuestions);

        given(parserConfig.getQuestionFilePath()).willReturn(null);
        assertThrows(CSVParseException.class, questionDaoCSV::getQuestions);
    }

}
