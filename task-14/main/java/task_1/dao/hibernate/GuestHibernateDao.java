package task_1.dao.hibernate;

import org.springframework.stereotype.Repository;
import task_1.dao.GuestDao;
import task_1.exceptions.DaoException;
import task_1.model.Guest;

import org.hibernate.query.Query;
import java.util.Optional;

@Repository
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
