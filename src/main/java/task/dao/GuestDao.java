package task.dao;

import task.model.Guest;

import java.util.Optional;

public interface GuestDao extends GenericDao<Guest, Long> {
    Optional<Guest> findByName(String name);
}
