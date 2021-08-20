package ru.otus.spring.dao;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ParserConfigCSVTest {
    private ParserConfig parserConfig;

    private static final char DELIMITER = '[';
    private static final int ANSWER_COLUMN_IDX = 1;
    private static final int QUESTION_ASK_QTY = 5;
    private static final int QUESTION_COLUMN_IDX = 0;
    private static final int FIRST_QUESTUON_COLUMN_IDX = 2;
    private static final String QUESTION_FILE_PATH = "test_questions_junit.csv";


    @DisplayName("Должны вернуться корректные параметры для чтения файла CSV")
    @Test
    void shouldReturnCorrectParamsForCSVParsing(){
        ParserConfigCSV parserConfig = ParserConfigCSV.builder()
                .csvDelimiter(DELIMITER)
                .correctAnswerColumnIdx(ANSWER_COLUMN_IDX)
                .questionFilePath(QUESTION_FILE_PATH)
                .questionColumnIdx(QUESTION_COLUMN_IDX)
                .questionAskQty(QUESTION_ASK_QTY)
                .firstQuestionColumnIdx(FIRST_QUESTUON_COLUMN_IDX).build();


        assertAll(
                () ->assertThat(parserConfig.getDelimiter()).isEqualTo(DELIMITER),
                () ->assertThat(parserConfig.getCorrectAnswerColumnIdx()).isEqualTo(ANSWER_COLUMN_IDX),
                () ->assertThat(parserConfig.getQuestionFilePath()).isEqualTo(QUESTION_FILE_PATH),
                () ->assertThat(parserConfig.getFirstQuestionColumnIdx()).isEqualTo(FIRST_QUESTUON_COLUMN_IDX),
                () ->assertThat(parserConfig.getQuestionAskQty()).isEqualTo(QUESTION_ASK_QTY),
                () ->assertThat(parserConfig.getQuestionColumnIdx()).isEqualTo(QUESTION_COLUMN_IDX)
        );
    }
}
