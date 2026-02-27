package task.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import task.dao.ServiceRecordDao;
import task.model.ServiceRecord;

import org.hibernate.query.Query;
import java.util.List;


@Repository
public class ServiceRecordHibernateDao extends AbstractHibernateDao<ServiceRecord, Long> implements ServiceRecordDao {
    public ServiceRecordHibernateDao(SessionFactory sessionFactory) {
        super(ServiceRecord.class, sessionFactory);
    }

    @Override
    public List<ServiceRecord> findByGuestId(long guestId) {
        String hql = "from ServiceRecord where guestId = :guestId";
        Query<ServiceRecord> query = getSession().createQuery(hql, ServiceRecord.class);
        query.setParameter("guestId", guestId);
        return query.list();
    }
}
