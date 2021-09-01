package ru.otus.spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.domain.Book;
import ru.otus.spring.dto.BookDTO;
import ru.otus.spring.dto.ErrorNotification;
import ru.otus.spring.exception.InvalidInputException;
import ru.otus.spring.exception.NotFoundException;
import ru.otus.spring.service.BookService;

@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;

    @GetMapping("/api/books")
    public ResponseEntity<List<Book>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/api/book/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") long id){
        return ResponseEntity.ok(bookService.findByID(id));
    }

    @PostMapping("/api/book")
    public ResponseEntity<Book> createBook(@RequestBody BookDTO book){
        return ResponseEntity.ok(bookService.create(book));
    }

    @PutMapping("/api/book")
    public ResponseEntity<Book> updateBook(@RequestBody BookDTO book){
        return ResponseEntity.ok(bookService.update(book));
    }

    @DeleteMapping("/api/book/{id}")
    public ResponseEntity<Book> deleteBookById(@PathVariable("id") long id){
        bookService.deleteByID(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorNotification> handleNotFound(NotFoundException e) {
        final HttpStatus responseStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorNotification(responseStatus.getReasonPhrase(), e.getLocalizedMessage()), responseStatus);
    }

    @ExceptionHandler({InvalidInputException.class})
    public ResponseEntity<ErrorNotification> handleInvalid(InvalidInputException e) {
        final HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorNotification(responseStatus.getReasonPhrase(), e.getLocalizedMessage()), responseStatus);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorNotification> handleEx(Exception e) {
        final HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorNotification(responseStatus.getReasonPhrase(), e.getLocalizedMessage()), responseStatus);
    }
}
