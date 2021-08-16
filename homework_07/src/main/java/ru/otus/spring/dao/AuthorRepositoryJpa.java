package ru.otus.spring.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Author;

@Repository
@RequiredArgsConstructor
public class AuthorRepositoryJpa implements AuthorRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public Author findById(long id) {
        TypedQuery<Author> query = em.createQuery("select a " +
                "from Author a " +
                "where a.id = :id", Author.class);
        query.setParameter("id", id);

        try{
            return query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    @Override
    public boolean isExistsById(long id) {
        try{
            TypedQuery<Integer> query = em.createQuery("select 1 from Author where exists (select 1 from Author a where a.id = :id)", Integer.class);
            query.setParameter("id", id);
            return query.getSingleResult() != null;
        } catch (NoResultException e){
            // Не найдена сущность
            return false;
        }
    }
}
