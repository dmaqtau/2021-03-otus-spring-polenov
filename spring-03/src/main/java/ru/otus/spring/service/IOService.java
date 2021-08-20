package ru.otus.spring.service;

import java.io.IOException;

public interface IOService {
    void out(String out);

    String readLine() throws IOException;
}
