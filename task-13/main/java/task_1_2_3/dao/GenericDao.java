package task_1_2_3.dao;

import task_1_2_3.exceptions.DaoException;

import java.util.List;
import java.util.Optional;


public interface GenericDao<T, ID> {
    T create(T entity) throws DaoException;

    Optional<T> findById(ID id) throws DaoException;

    List<T> findAll() throws DaoException;

    T update(T entity) throws DaoException;

    boolean deleteById(ID id) throws DaoException;

    default boolean delete(T entity, IdExtractor<T, ID> extractor) throws DaoException {
        if (entity == null) return false;
        return deleteById(extractor.getId(entity));
    }

    @FunctionalInterface
    interface IdExtractor<T, ID> {
        ID getId(T entity);
    }
}
