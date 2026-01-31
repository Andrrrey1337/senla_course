package task_1.dao;

import task_1.exceptions.DaoException;
import task_1.model.ServiceRecord;

import java.util.List;

public interface ServiceRecordDao extends GenericDao<ServiceRecord, Long> {
    List<ServiceRecord> findByGuestId(long guestId) throws DaoException;
}
