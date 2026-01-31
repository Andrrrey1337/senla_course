package task_1.dao;

import task_1.exceptions.DaoException;
import task_1.model.Service;

import java.util.Optional;

public interface ServiceDao extends GenericDao<Service, Long> {
    Optional<Service> findByName(String name) throws DaoException;
}
