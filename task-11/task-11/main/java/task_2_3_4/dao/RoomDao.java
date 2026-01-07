package task_2_3_4.dao;

import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.Room;

import java.util.Optional;

public interface RoomDao extends GenericDao<Room, Long> {
    Optional<Room> findByNumber(int number) throws DaoException;
}
