package task_1_2_3.dao;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Room;

import java.util.Optional;

public interface RoomDao extends GenericDao<Room, Long> {
    Optional<Room> findByNumber(int number) throws DaoException;
}
