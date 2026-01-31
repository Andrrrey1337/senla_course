package task_1.dao;

import task_1.exceptions.DaoException;
import task_1.model.Room;

import java.util.Optional;

public interface RoomDao extends GenericDao<Room, Long> {
    Optional<Room> findByNumber(int number) throws DaoException;
}
