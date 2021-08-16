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

@Repository
@RequiredArgsConstructor
public class BookRepositoryJpa implements BookRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public Long count() {
        TypedQuery<Long> query = em.createQuery("select count(b) from Book b", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() <= 0) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public Book findById(long id) {
        TypedQuery<Book> query = em.createQuery("select b from Book b " +
                "join fetch b.author " +
                "join fetch b.genre " +
                "where b.id = :id", Book.class);
        query.setParameter("id", id);

        try{
            return query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("select b from Book b " +
                "join fetch b.author " +
                "join fetch b.genre", Book.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete " +
                "from Book b " +
                "where b.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public boolean isExistsById(long id) {
        try{
            TypedQuery<Integer> query = em.createQuery("select 1 from Book where exists (select 1 from Book a where a.id = :id)", Integer.class);
            query.setParameter("id", id);
            return query.getSingleResult() != null;
        } catch (NoResultException e){
            // Не найдена сущность
            return false;
        }
    }
}
