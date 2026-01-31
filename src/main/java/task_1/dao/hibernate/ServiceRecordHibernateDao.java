package task_1.dao.hibernate;

import task_1.annotations.Component;
import task_1.annotations.Singleton;
import task_1.dao.ServiceRecordDao;
import task_1.exceptions.DaoException;
import task_1.model.ServiceRecord;

import org.hibernate.query.Query;
import java.util.List;


@Component
@Singleton
public class ServiceRecordHibernateDao extends AbstractHibernateDao<ServiceRecord, Long> implements ServiceRecordDao {
    public ServiceRecordHibernateDao() {
        super(ServiceRecord.class);
    }

    @Override
    public List<ServiceRecord> findByGuestId(long guestId) throws DaoException {
        String hql = "from ServiceRecord where guestId = :guestId";
        Query<ServiceRecord> query = getSession().createQuery(hql, ServiceRecord.class);
        query.setParameter("guestId", guestId);
        return query.list();
    }
}
