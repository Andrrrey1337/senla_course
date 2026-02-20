package task.dao;

import java.util.List;
import java.util.Optional;


public interface GenericDao<T, ID> {
    T create(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    T update(T entity);

    boolean deleteById(ID id);

    default boolean delete(T entity, IdExtractor<T, ID> extractor) {
        if (entity == null) return false;
        return deleteById(extractor.getId(entity));
    }

    @FunctionalInterface
    interface IdExtractor<T, ID> {
        ID getId(T entity);
    }
}
