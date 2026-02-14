package task.dao;

import task.exceptions.DaoException;
import task.model.Residence;

import java.util.List;

public interface ResidenceDao extends GenericDao<Residence, Long> {
    List<Residence> findLastByRoom(long roomId, int limit) throws DaoException;
}
