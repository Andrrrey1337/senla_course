package task_1.dao;

import task_1.exceptions.DaoException;
import task_1.model.Guest;

import java.util.Optional;

public interface GuestDao extends GenericDao<Guest, Long> {
    Optional<Guest> findByName(String name) throws DaoException;
}
