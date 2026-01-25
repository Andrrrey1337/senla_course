package task_1_2_3.dao.hibernate;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.dao.GuestDao;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Guest;

import org.hibernate.query.Query;
import java.util.Optional;

@Component
@Singleton
public class GuestHibernateDao extends AbstractHibernateDao<Guest, Long> implements GuestDao {
    public GuestHibernateDao() {
        super(Guest.class);
    }

    @Override
    public Optional<Guest> findByName(String name) throws DaoException {
        String hql = "from Guest where name = :name";
        Query<Guest> query = getSession().createQuery(hql, Guest.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }
}
