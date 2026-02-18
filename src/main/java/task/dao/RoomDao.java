package task.dao;

import task.model.Room;

import java.util.Optional;

public interface RoomDao extends GenericDao<Room, Long> {
    Optional<Room> findByNumber(int number);
}
