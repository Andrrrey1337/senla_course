package task_2_3_4.dao;

import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.Service;

import java.util.Optional;

public interface ServiceDao extends GenericDao<Service, Long> {
    Optional<Service> findByName(String name) throws DaoException;
}
