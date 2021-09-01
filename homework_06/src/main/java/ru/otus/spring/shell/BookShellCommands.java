package ru.otus.spring.shell;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.service.BookService;

@ShellComponent
@RequiredArgsConstructor
public class BookShellCommands {
    private final BookService bookService;
    private static final String DELIMITER = "=".repeat(100);
    
    static final String LIST_BOOKS_TEMPLATE = "Список всех книг (%d шт):";
    static final String FAILED_TO_GET_BOOKS_LIST_TEMPLATE = "Не удалось получить список всех книг: ";
    static final String DELETED_BOOK_TEMPLATE = "Удалена книга с идентификатором: %d";
    static final String FOUND_BOOK_BY_ID_TEMPLATE = "Получена с идентификатором %d:\nn%s";
    static final String NOT_FOUND_BOOK_BY_ID_TEMPLATE = "Не найдена книга с идентификатором: %d";
    static final String FAILED_TO_DELETE_BOOKS_TEMPLATE = "Не удалось удалить книгу: ";
    static final String FAILED_TO_GET_BY_ID_TEMPLATE = "Не удалось получить книгу: ";
    static final String FAILED_TO_UPDATE_BOOK_TEMPLATE = "Не удалось обновить книгу: ";
    static final String NOT_DELETED_BOOKS_TEMPLATE = "Не удалено ни одной книги с идентификатором %d";
    static final String BOOK_UPDATED_TEMPLATE = "Успешно обновили книгу с идентификатором %d. Обновлённый объект имеет вид:\n%s";
    static final String BOOK_CREATED_TEMPLATE = "Успешно добавили книгу. Новый объект имеет вид:\n%s";
    static final String FAILED_TO_CREATE_BOOK_TEMPLATE = "Не удалось добавить книгу: ";

    @ShellMethod(key = "list", value = "List all existing books")
    String listBooks(){
        try{
            List<Book> allBooks = bookService.getAll();
            return DELIMITER + "\n" +
                    String.format(LIST_BOOKS_TEMPLATE, allBooks.size()) + "\n" +
                    DELIMITER + "\n" +
                    allBooks.stream().map(Book::toString).collect(Collectors.joining("\n\n")) + "\n" +
                    DELIMITER;
        } catch (Exception e){
            return FAILED_TO_GET_BOOKS_LIST_TEMPLATE + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "delete", value = "Delete single book by id")
    String deleteById(@ShellOption({"-id", "id"}) long id){
        int deletedId = 0;
        try{
            deletedId = bookService.deleteByID(id);
        } catch (Exception e){
            return FAILED_TO_DELETE_BOOKS_TEMPLATE + e.getLocalizedMessage();
        }

        if(deletedId <= 0){
            return String.format(NOT_DELETED_BOOKS_TEMPLATE, id);
        } else{
            return String.format(DELETED_BOOK_TEMPLATE, id);
        }
    }

    @ShellMethod(key = "update", value = "Update single book")
    String updateBook(@ShellOption({"--id", "-I"}) long id,
                      @ShellOption(value = {"--name", "-N"}, defaultValue = "") String bookName,
                      @ShellOption(value = {"--authorId", "-A"}, defaultValue = "0") long authorId,
                      @ShellOption(value = {"--genreId", "-G"}, defaultValue = "0") long genreId,
                      @ShellOption(value = {"--description", "-D"}, defaultValue = "") String description
    ){
        try{
            String bookStr = getBookInfo(bookService.update(id, bookName, description, authorId, genreId));
            return String.format(BOOK_UPDATED_TEMPLATE, id, bookStr);
        } catch (Exception e){
            return FAILED_TO_UPDATE_BOOK_TEMPLATE + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "create", value = "Create single book")
    String createBook(@ShellOption(value = {"--name", "-N"}) String bookName,
                      @ShellOption(value = {"--authorId", "-A"}) long authorId,
                      @ShellOption(value = {"--genreId", "-G"}) long genreId,
                      @ShellOption(value = {"--description", "-D"}, defaultValue = "") String description
    ){
        try{
            String bookStr = getBookInfo(bookService.create(bookName, description, authorId, genreId));
            return String.format(BOOK_CREATED_TEMPLATE, bookStr);
        } catch (Exception e){
            return FAILED_TO_CREATE_BOOK_TEMPLATE + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "getById", value = "Get single book by id")
    String getById(@ShellOption({"-id", "id"}) long id){
        try{
            Book book =  bookService.getByID(id);
            if(book == null){
                return String.format(NOT_FOUND_BOOK_BY_ID_TEMPLATE, id);
            }
            return String.format(FOUND_BOOK_BY_ID_TEMPLATE, id, getBookInfo(book));
        } catch (Exception e){
            return FAILED_TO_GET_BY_ID_TEMPLATE + e.getLocalizedMessage();
        }
    }

    static String getBookInfo(Book book){
        return String.format(
                "id = [%d],\nИмя = [%s],\nАвтор = [%s],\nЖанр = [%s],\nОписание = [%s]",
                book.getId(), book.getBookName(),
                getAuthorInfo(book.getAuthor()), book.getGenre().getGenreName(), book.getDescription()
        );
    }

    static String getAuthorInfo(Author author){
        if(author.getPatronymic() == null){
            return String.format("%s %s", author.getSurname(), author.getAuthorName());
        }
        return String.format("%s %s %s", author.getSurname(), author.getAuthorName(), author.getPatronymic());
    }
}
