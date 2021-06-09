package ru.otus.spring.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
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
        IOServiceImpl ioService = new IOServiceImpl(new PrintStream(outputStreamCaptor), new BufferedInputStream(System.in));
        ioService.out(OUT_MESSAGE);
        assertThat(outputStreamCaptor.toString().trim()).hasToString(OUT_MESSAGE);
    }

    @DisplayName("Должнен принять в обработку входящее сообщение")
    @Test
    void shouldConsumeInMessage() {
        IOServiceImpl ioService = new IOServiceImpl(System.out, new BufferedInputStream(new ByteArrayInputStream(IN_MESSAGE.getBytes())));
        assertThat(ioService.readLine()).isEqualTo(IN_MESSAGE);
    }
}
