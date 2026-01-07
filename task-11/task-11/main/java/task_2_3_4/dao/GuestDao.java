package task_2_3_4.dao;

import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.Guest;

import java.util.Optional;

public interface GuestDao extends GenericDao<Guest, Long> {
    Optional<Guest> findByName(String name) throws DaoException;
}
