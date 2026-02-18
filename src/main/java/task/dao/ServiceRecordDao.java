package task.dao;

import task.model.ServiceRecord;

import java.util.List;

public interface ServiceRecordDao extends GenericDao<ServiceRecord, Long> {
    List<ServiceRecord> findByGuestId(long guestId);
}
