package task.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import task.dao.ServiceDao;
import task.model.Service;

import org.hibernate.query.Query;
import java.util.Optional;

@Repository
public class ServiceHibernateDao extends AbstractHibernateDao<Service, Long> implements ServiceDao {
    public ServiceHibernateDao(SessionFactory sessionFactory) {
        super(Service.class, sessionFactory);
    }

    @Override
    public Optional<Service> findByName(String name) {
        String hql = "from Service where name = :name";
        Query<Service> query = getSession().createQuery(hql, Service.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }
}
