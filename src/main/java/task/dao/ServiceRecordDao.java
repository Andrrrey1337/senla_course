package task.dao;

import task.exceptions.DaoException;
import task.model.ServiceRecord;

import java.util.List;

public interface ServiceRecordDao extends GenericDao<ServiceRecord, Long> {
    List<ServiceRecord> findByGuestId(long guestId) throws DaoException;
}
