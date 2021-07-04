package ru.otus.spring.service;

import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IOServiceImpl implements IOService {

    private final PrintStream printStream;
    private final Scanner scanner;

    public IOServiceImpl(@Value("#{T(System).out}") PrintStream printStream,
                         @Value("#{T(System).in}") BufferedInputStream inputStream){
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public void out(String out) {
        printStream.println(out);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }
}
