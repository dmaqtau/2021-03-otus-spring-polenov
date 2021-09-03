package ru.otus.spring.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.dao.BookCommentRepository;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;
import ru.otus.spring.dto.BookCommentDTO;
import ru.otus.spring.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements BookCommentService {
    private final LibraryObjectValidator validator;
    private final BookCommentRepository commentRepository;
    private final BookService bookService;
            
    @Override
    @Transactional
    public BookComment addComment(BookCommentDTO commentDTO) {
        validator.validateBookComment(commentDTO);
        Long bookId = commentDTO.getBookId();

        bookService.checkBookExists(bookId);
        return commentRepository.save(new BookComment(0, new Book(bookId), commentDTO.getUserLogin(), commentDTO.getComment()));
    }

    @Override
    @Transactional
    public void deleteCommentById(long commentId) {
        if(!commentRepository.existsById(commentId)){
            throw new NotFoundException("Не найден комментарий по идентификатору: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentsByBookId(long bookId) {
        bookService.checkBookExists(bookId);

        List<BookComment> comments = commentRepository.findAllByBookId(bookId);

        if(CollectionUtils.isEmpty(comments)){
            throw new NotFoundException("У книги с идентификатором " + bookId + " нет комментариев.");
        }
        commentRepository.deleteAllByBookId(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookComment> findComments(long bookId) {
        bookService.checkBookExists(bookId);
        return commentRepository.findAllByBookId(bookId);
    }
}
