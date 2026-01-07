package task_2_3_4.dao;

import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.ServiceRecord;

import java.util.List;

public interface ServiceRecordDao extends GenericDao<ServiceRecord, Long> {
    List<ServiceRecord> findByGuestId(long guestId) throws DaoException;
}
