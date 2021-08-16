package ru.otus.spring.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;

@Repository
@RequiredArgsConstructor
public class BookCommentRepositoryJpa implements BookCommentRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public BookComment save(String userLogin, String commentValue, long bookId) {
        BookComment comment = new BookComment(userLogin, commentValue);
        comment.setBook(em.getReference(Book.class, bookId));

        if (comment.getId() <= 0) {
            em.persist(comment);
            return comment;
        } else {
            return em.merge(comment);
        }
    }

    @Override
    public List<BookComment> findByBookId(long bookId) {
        TypedQuery<BookComment> query = em.createQuery("select c " +
                "from BookComment c join fetch c.book " +
                "where c.book.id = :bookId", BookComment.class);
        query.setParameter("bookId", bookId);

        try{
            return query.getResultList();
        } catch (NoResultException e){
            return List.of();
        }
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete " +
                "from BookComment b " +
                "where b.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public void deleteByBookId(long bookId) {
        Query query = em.createQuery("delete " +
                "from BookComment b " +
                "where b.book.id = :bookId");
        query.setParameter("bookId", bookId);
        query.executeUpdate();
    }

    @Override
    public boolean isExistsById(long id) {
        try{
            TypedQuery<Integer> query = em.createQuery("select 1 from BookComment where exists (select 1 from BookComment a where a.id = :id)", Integer.class);
            query.setParameter("id", id);
            return query.getSingleResult() != null;
        } catch (NoResultException e){
            // Не найдена сущность
            return false;
        }
    }
}
