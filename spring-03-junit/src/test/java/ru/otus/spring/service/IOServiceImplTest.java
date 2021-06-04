package ru.otus.spring.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IOServiceImplTest {
    private static final String IN_MESSAGE = "test_in_message";
    private static final String OUT_MESSAGE = "test_out_message";


    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @AfterEach
    void tearDown() {
        System.setIn(standardIn);
        System.setOut(standardOut);
    }

    @DisplayName("Должны вывести сообщение в консоль")
    @Test
    void shouldDisplayOutMessage(){
        System.setOut(new PrintStream(outputStreamCaptor));
        IOServiceImpl ioService = new IOServiceImpl(System.out, new BufferedInputStream(System.in));
        ioService.out(OUT_MESSAGE);
        assertThat(outputStreamCaptor.toString().trim()).hasToString(OUT_MESSAGE);
    }

    @DisplayName("Должнен принять в обработку входящее сообщение")
    @Test
    void shouldConsumeInMessage() {
        ByteArrayInputStream in = new ByteArrayInputStream(IN_MESSAGE.getBytes());
        System.setIn(in);

        IOServiceImpl ioService = new IOServiceImpl(System.out, new BufferedInputStream(System.in));
        assertThat(ioService.readLine()).isEqualTo(IN_MESSAGE);
    }
}
