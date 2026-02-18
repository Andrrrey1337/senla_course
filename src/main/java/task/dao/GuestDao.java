package task.dao;

import task.exceptions.DaoException;
import task.model.Guest;

import java.util.Optional;

public interface GuestDao extends GenericDao<Guest, Long> {
    Optional<Guest> findByName(String name) throws DaoException;
}
