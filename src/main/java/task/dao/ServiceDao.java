package task.dao;

import task.exceptions.DaoException;
import task.model.Service;

import java.util.Optional;

public interface ServiceDao extends GenericDao<Service, Long> {
    Optional<Service> findByName(String name) throws DaoException;
}
