package task.util;

import org.springframework.stereotype.Component;
import org.hibernate.SessionFactory;
import task.exceptions.DaoException;
import task.util.constants.SqlConstants;

@Component
public class IdSyncManager {
    private IdGenerator idGenerator;
    private SessionFactory sessionFactory;

    public IdSyncManager(IdGenerator idGenerator, SessionFactory sessionFactory) {
        this.idGenerator = idGenerator;
    }

    public void sync() {
        long max = 0;
        try {
            max = Math.max(max, maxId(SqlConstants.T_ROOMS));
            max = Math.max(max, maxId(SqlConstants.T_GUESTS));
            max = Math.max(max, maxId(SqlConstants.T_SERVICES));
            max = Math.max(max, maxId(SqlConstants.T_RESIDENCES));
            max = Math.max(max, maxId(SqlConstants.T_SERVICE_RECORDS));

            idGenerator.setNext(max + 1);
            System.out.println("IdGenerator синхронизирован: nextId=" + (max + 1));
        } catch (DaoException e) {
            throw new RuntimeException("Не удалось синхронизировать IdGenerator: " + e.getMessage(), e);
        }
    }

    private long maxId(String entityName) throws DaoException {
        String hql = "select coalesce(max(id), 0) from " + entityName;
        org.hibernate.Session session = sessionFactory.getCurrentSession();
        Long result = session.createQuery(hql, Long.class).uniqueResult();
        return result != null ? result : 0;
    }
}
