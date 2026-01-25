package task_1_2_3.dao;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Residence;

import java.util.List;

public interface ResidenceDao extends GenericDao<Residence, Long> {
    List<Residence> findLastByRoom(long roomId, int limit) throws DaoException;
}
