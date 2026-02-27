package task.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import task.dao.GuestDao;
import task.model.Guest;

import org.hibernate.query.Query;
import java.util.Optional;

@Repository
public class GuestHibernateDao extends AbstractHibernateDao<Guest, Long> implements GuestDao {
    public GuestHibernateDao(SessionFactory sessionFactory) {
        super(Guest.class, sessionFactory);
    }

    @Override
    public Optional<Guest> findByName(String name) {
        String hql = "from Guest where name = :name";
        Query<Guest> query = getSession().createQuery(hql, Guest.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }
}
