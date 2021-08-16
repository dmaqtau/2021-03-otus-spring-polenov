package ru.otus.spring.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.spring.domain.Genre;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryJpa implements GenreRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public boolean isExistsById(long id) {
        try{
            TypedQuery<Integer> query = em.createQuery("select 1 from Genre where exists (select 1 from Genre a where a.id = :id)", Integer.class);
            query.setParameter("id", id);
            return query.getSingleResult() != null;
        } catch (NoResultException e){
            // Не найдена сущность
            return false;
        }
    }

    @Override
    public Genre findById(long id) {
        TypedQuery<Genre> query = em.createQuery("select g " +
                "from Genre g " +
                "where g.id = :id", Genre.class);
        query.setParameter("id", id);

        try{
            return query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }
}
