package task_1.dao.hibernate;

import org.springframework.stereotype.Repository;
import task_1.dao.ServiceDao;
import task_1.exceptions.DaoException;
import task_1.model.Service;

import org.hibernate.query.Query;
import java.util.Optional;

@Repository
public class ServiceHibernateDao extends AbstractHibernateDao<Service, Long> implements ServiceDao {
    public ServiceHibernateDao() {
        super(Service.class);
    }

    @Override
    public Optional<Service> findByName(String name) throws DaoException {
        String hql = "from Service where name = :name";
        Query<Service> query = getSession().createQuery(hql, Service.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }
}
