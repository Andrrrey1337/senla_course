package task_1_2_3.dao;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Guest;

import java.util.Optional;

public interface GuestDao extends GenericDao<Guest, Long> {
    Optional<Guest> findByName(String name) throws DaoException;
}
