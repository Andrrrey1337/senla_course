package task.dao;

import task.model.Residence;

import java.util.List;

public interface ResidenceDao extends GenericDao<Residence, Long> {
    List<Residence> findLastByRoom(long roomId, int limit);
}
