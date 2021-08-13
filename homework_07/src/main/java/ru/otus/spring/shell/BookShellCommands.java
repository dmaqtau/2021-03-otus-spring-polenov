package ru.otus.spring.shell;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;
import ru.otus.spring.service.BookService;

@ShellComponent
@RequiredArgsConstructor
public class BookShellCommands {
    private final BookService bookService;
    private static final String DELIMITER = "=".repeat(100);
    
    static final String LIST_BOOKS = "Список всех книг (%d шт):";
    static final String FAILED_TO_GET_BOOKS_LIST = "Не удалось получить список всех книг: ";
    static final String DELETED_BOOK = "Удалена книга с идентификатором: %d";
    static final String FOUND_BOOK_BY_ID = "Получена с идентификатором %d:\n%s";
    static final String FOUND_COMMENTS_FOR_BOOK = "Получены комментарии для книги с идентификатором %d:\n%s";
    static final String ADDED_COMMENT_FOR_BOOK = "Добавлен комментарий для книги с идентификатором %d:\n%s";
    static final String NOT_FOUND_BOOK_BY_ID = "Не найдена книга с идентификатором: %d";
    static final String FAILED_TO_DELETE_BOOKS = "Не удалось удалить книгу: ";
    static final String FAILED_TO_GET_BY_ID = "Не удалось получить книгу: ";
    static final String FAILED_TO_GET_BOOK_COMMENTS = "Не удалось получить комментарии для книги: ";
    static final String FAILED_TO_ADD_BOOK_COMMENT = "Не удалось добавить комментарии для книги: ";
    static final String FAILED_TO_DELETE_COMMENT = "Не удалось удалить комментарии для книги: ";
    static final String FAILED_TO_UPDATE_BOOK = "Не удалось обновить книгу: ";
    static final String BOOK_UPDATED = "Успешно обновили книгу с идентификатором %d. Обновлённый объект имеет вид:\n%s";
    static final String BOOK_CREATED = "Успешно добавили книгу. Новый объект имеет вид:\n%s";
    static final String FAILED_TO_CREATE_BOOK = "Не удалось добавить книгу: ";
    static final String COMMENT_DELETE_NO_PARAMS = "Укажите один из двух параметров: ID книги или ID комментария";
    static final String COMMENT_DELETED_MSG = "Комментарий успешно удалён.";
    static final String COMMENT_BY_BOOK_ID_DELETED_MSG = "Комментарии для книги с идентификатором %d успешно удалены.";

    @ShellMethod(key = "list", value = "List all existing books")
    String listBooks(){
        try{
            List<Book> allBooks = bookService.getAll();
            return DELIMITER + "\n" +
                    String.format(LIST_BOOKS, allBooks.size()) + "\n" +
                    DELIMITER + "\n" +
                    allBooks.stream().map(BookShellCommands::getBookInfo).collect(Collectors.joining("\n\n")) + "\n" +
                    DELIMITER;
        } catch (Exception e){
            return FAILED_TO_GET_BOOKS_LIST + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "delete", value = "Delete single book by id")
    String deleteById(@ShellOption({"-id", "id"}) long id){
        try{
            bookService.deleteByID(id);
        } catch (Exception e){
            return FAILED_TO_DELETE_BOOKS + e.getLocalizedMessage();
        }
        return String.format(DELETED_BOOK, id);
    }

    @ShellMethod(key = "update", value = "Update single book")
    String updateBook(@ShellOption({"--id", "-I"}) long id,
                      @ShellOption(value = {"--name", "-N"}) String bookName,
                      @ShellOption(value = {"--authorId", "-A"}, defaultValue = "0") long authorId,
                      @ShellOption(value = {"--genreId", "-G"}, defaultValue = "0") long genreId,
                      @ShellOption(value = {"--description", "-D"}) String description
    ){
        try{
            String bookStr = getBookInfo(bookService.update(id, bookName, description, authorId, genreId));
            return String.format(BOOK_UPDATED, id, bookStr);
        } catch (Exception e){
            return FAILED_TO_UPDATE_BOOK + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "create", value = "Create single book")
    String createBook(@ShellOption(value = {"--name", "-N"}) String bookName,
                      @ShellOption(value = {"--authorId", "-A"}) long authorId,
                      @ShellOption(value = {"--genreId", "-G"}) long genreId,
                      @ShellOption(value = {"--description", "-D"}) String description
    ){
        try{
            String bookStr = getBookInfo(bookService.create(bookName, description, authorId, genreId));
            return String.format(BOOK_CREATED, bookStr);
        } catch (Exception e){
            return FAILED_TO_CREATE_BOOK + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "getById", value = "Get single book by id")
    String getById(@ShellOption({"--id", "-I"}) long id){
        try{
            Book book =  bookService.getByID(id);
            if(book == null){
                return String.format(NOT_FOUND_BOOK_BY_ID, id);
            }
            return String.format(FOUND_BOOK_BY_ID, id, getBookInfo(book));
        } catch (Exception e){
            return FAILED_TO_GET_BY_ID + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "getComments", value = "Get comments for book")
    String getComments(@ShellOption({"--bookId", "-I"}) long bookId){
        try{
            List<BookComment> comments = bookService.getComments(bookId);
            return String.format(FOUND_COMMENTS_FOR_BOOK, bookId, getCommentInfo(comments));
        } catch (Exception e){
            return FAILED_TO_GET_BOOK_COMMENTS + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "addComment", value = "Add comment for book")
    String getComments(@ShellOption({"--bookId", "-I"}) long bookId,
                       @ShellOption({"--userLogin", "-U"}) String userLogin,
                       @ShellOption({"--comment", "-C"}) String commentValue
    ){
        try{
            BookComment comment = bookService.addComment(bookId, userLogin, commentValue);
            return String.format(ADDED_COMMENT_FOR_BOOK, bookId, getCommentInfo(List.of(comment)));
        } catch (Exception e){
            return FAILED_TO_ADD_BOOK_COMMENT + e.getLocalizedMessage();
        }
    }

    @ShellMethod(key = "deleteComment", value = "Delete comment")
    String deleteComment(@ShellOption(value = {"--id", "-I"}, defaultValue = "0") long id,
                         @ShellOption(value = {"--bookId", "-B"}, defaultValue = "0") long bookId){
        if((id <=0 && bookId <=0) || (id > 0 && bookId > 0)){
            return COMMENT_DELETE_NO_PARAMS;
        }

        try{
            if(id > 0){
                bookService.deleteCommentById(id);
                return COMMENT_DELETED_MSG;
            }

            bookService.deleteCommentByBookId(bookId);
            return String.format(COMMENT_BY_BOOK_ID_DELETED_MSG, bookId);
        } catch (Exception e){
            return FAILED_TO_DELETE_COMMENT + e.getLocalizedMessage();
        }
    }
    
    static String getBookInfo(Book book){
        return String.format(
                "id = [%d],\nИмя = [%s],\nАвтор = [%s],\nЖанр = [%s],\nОписание = [%s], \nКомментарии = (%d шт)",
                book.getId(), book.getName(),
                getAuthorInfo(book.getAuthor()), book.getGenre().getName(),
                getEmptyStrIfBlank(book.getDescription()),
                CollectionUtils.isEmpty(book.getComments()) ? 0: book.getComments().size()
        );
    }

    private static String getCommentInfo(List<BookComment> comments){
        return comments.stream()
                .map(c -> String.format("id = [%d], bookId = [%d], userLogin = [%s], comment = [%s]",
                        c.getId(), c.getBook().getId(), c.getUserLogin(), c.getComment()))
                .collect(Collectors.joining("\n"));
    }

    private static String getAuthorInfo(Author author){
        if(author.getPatronymic() == null){
            return String.format("%s %s", author.getSurname(), author.getName());
        }
        return String.format("%s %s %s", author.getSurname(), author.getName(), author.getPatronymic());
    }

    private static String getEmptyStrIfBlank(String str){
        return StringUtils.isBlank(str) ? "<пусто>": str;
    }
}
