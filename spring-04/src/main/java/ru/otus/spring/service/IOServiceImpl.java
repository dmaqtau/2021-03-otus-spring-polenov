package ru.otus.spring.service;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class IOServiceImpl implements IOService {

    private final PrintStream printStream;
    private final Scanner scanner;

    public IOServiceImpl(PrintStream consoleOutputStream,
                         InputStream consoleInputStream){
        this.printStream = consoleOutputStream;
        this.scanner = new Scanner(consoleInputStream);
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
