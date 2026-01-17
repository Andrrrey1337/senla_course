package task_2_3_4.dao;

import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.Residence;

import java.util.List;

public interface ResidenceDao extends GenericDao<Residence, Long> {
    List<Residence> findLastByRoom(long roomId, int limit) throws DaoException;
}
