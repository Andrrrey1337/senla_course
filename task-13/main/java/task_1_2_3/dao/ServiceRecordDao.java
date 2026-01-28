package task_1_2_3.dao;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.ServiceRecord;

import java.util.List;

public interface ServiceRecordDao extends GenericDao<ServiceRecord, Long> {
    List<ServiceRecord> findByGuestId(long guestId) throws DaoException;
}
