package ru.otus.spring.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
class IOServiceImplTest {
    @MockBean
    private PrintStream mockedPrintStream;
    @Autowired
    private IOServiceImpl ioService;

    private static final String IN_MESSAGE = "test_in_message";
    private static final String OUT_MESSAGE = "test_out_message";

    private static InputStream standardIn = System.in;

    private InputStream mockedInputStream = new ByteArrayInputStream(IN_MESSAGE.getBytes());

    @BeforeAll
    static void init(){
        InputStream mockedInputStream = new ByteArrayInputStream(IN_MESSAGE.getBytes());
        System.setIn(mockedInputStream);
    }

    @AfterAll
    static void tearDown(){
        System.setIn(standardIn);
    }

    @DisplayName("Должны вывести сообщение в консоль")
    @Test
    void shouldDisplayOutMessage(){
        ioService.out(OUT_MESSAGE);
        verify(mockedPrintStream).println(OUT_MESSAGE);
    }

    @DisplayName("Должнен принять в обработку входящее сообщение")
    @Test
    void shouldConsumeInMessage() throws IOException {
        System.setIn(mockedInputStream);
        assertThat(ioService.readLine()).isEqualTo(IN_MESSAGE);
    }
}
