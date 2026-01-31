package task_1.dao;

import task_1.exceptions.DaoException;
import task_1.model.Residence;

import java.util.List;

public interface ResidenceDao extends GenericDao<Residence, Long> {
    List<Residence> findLastByRoom(long roomId, int limit) throws DaoException;
}
