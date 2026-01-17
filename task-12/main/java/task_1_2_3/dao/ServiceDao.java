package task_1_2_3.dao;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Service;

import java.util.Optional;

public interface ServiceDao extends GenericDao<Service, Long> {
    Optional<Service> findByName(String name) throws DaoException;
}
