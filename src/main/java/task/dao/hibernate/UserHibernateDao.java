package task.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import task.dao.UserDao;
import task.model.User;

import java.util.Optional;

public class UserHibernateDao extends AbstractHibernateDao<User, Long> implements UserDao {
    protected UserHibernateDao(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String hql = "from User where username = :username";
        Query<User> query = getSession().createQuery(hql, User.class);
        query.setParameter("username", username);
        return query.uniqueResultOptional();
    }
}
