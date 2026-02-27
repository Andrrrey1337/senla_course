package task.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import task.dao.GenericDao;

import java.util.List;
import java.util.Optional;


public abstract class AbstractHibernateDao<T, ID> implements GenericDao<T, ID> {

    private final Class<T> clazz;
    protected final SessionFactory sessionFactory;

    protected AbstractHibernateDao(Class<T> clazz, SessionFactory sessionFactory) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
    }


    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    @Override
    public T create(T entity) {
        getSession().persist(entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getSession().get(clazz, id));
    }

    @Override
    public List<T> findAll() {
        return getSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    @Override
    public T update(T entity) {
        return getSession().merge(entity);
    }

    @Override
    public boolean deleteById(ID id) {
        T entity = getSession().get(clazz, id);
        if (entity != null) {
            getSession().remove(entity);
            return true;
        }
        return false;
    }
}
