package task.dao.hibernate;

import org.hibernate.Session;
import task.dao.GenericDao;
import task.db.ConnectionManager;
import task.exceptions.DaoException;

import java.util.List;
import java.util.Optional;


public abstract class AbstractHibernateDao<T, ID> implements GenericDao<T, ID> {

    private final Class<T> clazz;

    protected AbstractHibernateDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected Session getSession() throws DaoException {
        return ConnectionManager.getInstance().getSession();
    }

    @Override
    public T create(T entity) throws DaoException {
        getSession().persist(entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) throws DaoException {
        return Optional.ofNullable(getSession().get(clazz, id));
    }

    @Override
    public List<T> findAll() throws DaoException {
        return getSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    @Override
    public T update(T entity) throws DaoException {
        return getSession().merge(entity);
    }

    @Override
    public boolean deleteById(ID id) throws DaoException {
        T entity = getSession().get(clazz, id);
        if (entity != null) {
            getSession().remove(entity);
            return true;
        }
        return false;
    }
}
